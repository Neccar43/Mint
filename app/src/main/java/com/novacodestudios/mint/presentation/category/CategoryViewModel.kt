package com.novacodestudios.mint.presentation.category

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.novacodestudios.mint.data.local.entitiy.ExpenseCategory
import com.novacodestudios.mint.data.local.entitiy.IncomeCategory
import com.novacodestudios.mint.data.repository.MintRepository
import com.novacodestudios.mint.model.Category
import com.novacodestudios.mint.presentation.addition.AdditionViewModel
import com.novacodestudios.mint.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CategoryViewModel @Inject constructor(
    private val repository: MintRepository
) : ViewModel() {

    var state by mutableStateOf(CategoryState())
        private set

    private val _eventFlow = MutableSharedFlow<String>()
    val eventFlow = _eventFlow.asSharedFlow()

    init {
        getCategories()
    }

    fun onEvent(event: CategoryEvent) {
        when (event) {
            CategoryEvent.OnAddCategory -> addCategory()
            is CategoryEvent.OnCategoryNameChange -> state = state.copy(categoryName = event.name)
            is CategoryEvent.OnCategoryTypeChange -> {
                state = state.copy(isExpenseCategory = event.isExpenseCategory)
                getCategories()
            }
            is CategoryEvent.OnCategoryDelete -> deleteCategory(event.category)
            is CategoryEvent.OnCategoryColorChange -> state = state.copy(categoryColor = event.color)
        }
    }

    private fun deleteCategory(category: Category) {
        viewModelScope.launch {
            if (state.isExpenseCategory){
                deleteExpenseCategory(category)
                return@launch
            }
            deleteIncomeCategory(category)
        }
    }

    private fun deleteIncomeCategory(category: Category) {
        viewModelScope.launch {
            repository.deleteIncomeCategory(IncomeCategory(category.id,category.name,category.color)).collectLatest { resource->
                state = when (resource) {
                    is Resource.Error -> {
                        _eventFlow.emit(resource.exception.message ?:"error")
                        state.copy(isLoading = false, error = resource.exception.message)
                    }
                    Resource.Loading -> state.copy(isLoading = true, error = null)
                    is Resource.Success -> {
                        _eventFlow.emit("Category deleted successfully")
                        state.copy(isLoading = false, error = null)
                    }
                }
            }
        }
    }

    private fun deleteExpenseCategory(category: Category) {
        viewModelScope.launch {
            repository.deleteExpenseCategory(ExpenseCategory(category.id,category.name,category.color)).collectLatest { resource->
                state = when (resource) {
                    is Resource.Error -> {
                        _eventFlow.emit(resource.exception.message ?:"error")
                        state.copy(isLoading = false, error = resource.exception.message)
                    }
                    Resource.Loading -> state.copy(isLoading = true, error = null)
                    is Resource.Success -> {
                        _eventFlow.emit("Category deleted successfully")
                        state.copy(isLoading = false, error = null)
                    }
                }
            }
        }
    }

    private fun addCategory() {
        viewModelScope.launch {
            val hasNull = listOf(
                state.categoryColor,
                state.categoryName,
            ).any { it == null }
            if (hasNull) {
                state = state.copy(isLoading = false, error = "Fields cannot be empty")
                _eventFlow.emit(state.error ?: "error")
                return@launch
            }
            if (state.isExpenseCategory) {
                addExpenseCategory()
                return@launch
            }
            addIncomeCategory()
        }

    }

    private fun addIncomeCategory() {
        viewModelScope.launch {
            repository.upsertIncomeCategory(
                IncomeCategory(
                    name = state.categoryName!!,
                    color = state.categoryColor!!,
                )
            ).collectLatest { resource ->
                state = when (resource) {
                    is Resource.Error -> state.copy(
                        isLoading = false,
                        error = resource.exception.message
                    )

                    Resource.Loading -> state.copy(isLoading = true, error = null)
                    is Resource.Success -> {
                        _eventFlow.emit("Category added successfully")
                        state.copy(
                            isLoading = false,
                            error = null,
                            categoryName = null,
                        )
                    }
                }

            }
        }
    }

    private fun addExpenseCategory() {
        viewModelScope.launch {
            repository.upsertExpenseCategory(
                ExpenseCategory(
                    name = state.categoryName!!,
                    color = state.categoryColor!!,
                )
            ).collectLatest { resource ->
                state = when (resource) {
                    is Resource.Error -> state.copy(
                        isLoading = false,
                        error = resource.exception.message
                    )

                    Resource.Loading -> state.copy(isLoading = true, error = null)
                    is Resource.Success -> {
                        _eventFlow.emit("Category added successfully")
                        state.copy(
                            isLoading = false,
                            error = null,
                            categoryName = null,
                        )
                    }
                }

            }
        }
    }

    private fun getCategories() {
        if (state.isExpenseCategory) {
            getExpenseCategories()
            return
        }
        getIncomeCategories()

    }

    private fun getIncomeCategories() {
        viewModelScope.launch {
            repository.getIncomeCategories().collectLatest { resource ->
                state = when (resource) {
                    is Resource.Error -> state.copy(
                        isLoading = false,
                        error = resource.exception.message
                    )

                    Resource.Loading -> state.copy(isLoading = true, error = null)
                    is Resource.Success -> state.copy(
                        isLoading = false,
                        error = null,
                        incomeCategories = resource.data
                    )
                }

            }
        }
    }

    private fun getExpenseCategories() {
        viewModelScope.launch {
            repository.getExpenseCategories().collectLatest { resource ->
                state = when (resource) {
                    is Resource.Error -> state.copy(
                        isLoading = false,
                        error = resource.exception.message
                    )

                    Resource.Loading -> state.copy(isLoading = true, error = null)
                    is Resource.Success -> state.copy(
                        isLoading = false,
                        error = null,
                        expenseCategories = resource.data
                    )
                }

            }
        }
    }

}

data class CategoryState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val isExpenseCategory: Boolean = true,
    val categoryName: String? = null,
    val categoryColor: Int= Category.colors.random(),
    val expenseCategories: List<ExpenseCategory> = emptyList(),
    val incomeCategories: List<IncomeCategory> = emptyList(),
)

sealed class CategoryEvent {
    data class OnCategoryNameChange(val name: String) : CategoryEvent()
    data object OnAddCategory : CategoryEvent()
    data class OnCategoryTypeChange(val isExpenseCategory: Boolean):CategoryEvent()
    data class OnCategoryColorChange(val color:Int):CategoryEvent()
    data class OnCategoryDelete(val category: Category):CategoryEvent()
}