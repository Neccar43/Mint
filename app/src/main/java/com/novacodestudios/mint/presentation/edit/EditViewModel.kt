package com.novacodestudios.mint.presentation.edit

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.novacodestudios.mint.data.local.entitiy.Expense
import com.novacodestudios.mint.data.local.entitiy.Income
import com.novacodestudios.mint.data.repository.MintRepository
import com.novacodestudios.mint.model.Category
import com.novacodestudios.mint.model.Transaction
import com.novacodestudios.mint.presentation.addition.AdditionViewModel
import com.novacodestudios.mint.util.Const.IS_EXPENSE
import com.novacodestudios.mint.util.Const.TRANSACTION_ID
import com.novacodestudios.mint.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class EditViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: MintRepository,
) : ViewModel() {
    var state by mutableStateOf(EditState())
        private set

    private val _eventFlow = MutableSharedFlow<UIEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    init {
        savedStateHandle.apply {
            get<Int>(TRANSACTION_ID)?.let { id ->
                state = state.copy(id = id)
                get<Boolean>(IS_EXPENSE)?.let { isExpense ->
                    state = state.copy(isExpense = isExpense)
                    Log.d(TAG, "init: $id $isExpense")
                    getCategories()
                    if (isExpense) {
                        getExpenseAndCategory(id)
                    } else {
                        getIncomeAndCategory(id)
                    }
                }
            }
        }
    }

    fun onEvent(event: EditEvent){
        when (event) {
            is EditEvent.OnAmountChange -> state = state.copy(amount = event.amount)
            is EditEvent.OnCategoryChange -> state = state.copy(category = event.category)
            is EditEvent.OnDateChange -> state = state.copy(date = event.date)
            is EditEvent.OnNameChange -> state = state.copy(name = event.name)
            EditEvent.OnUpdate -> update()
            is EditEvent.OnDescriptionChange -> state = state.copy(description = event.description)
        }
    }

    private fun update() {
        viewModelScope.launch {
            val hasNull = listOf(
                state.name,
                state.amount,
                state.date,
                state.description,
                state.category
            ).any { it == null }
            if (hasNull) {
                state = state.copy(isLoading = false, error = "Fields cannot be empty")
                _eventFlow.emit(UIEvent.ShowSnackbar(state.error!!))
                return@launch
            }
            if (state.isExpense !=null&& state.isExpense!!){
                updateExpense()
                return@launch
            }
            updateIncome()
        }
    }
    private fun getCategories() {
        if (state.isExpense == true) {
            getExpenseCategories()
            return
        }
        getIncomeCategories()

    }

    private fun getIncomeCategories() {
        viewModelScope.launch {
            repository.getIncomeCategories().collectLatest { resource ->
                state = when (resource) {
                    is Resource.Error -> state.copy(isLoading = false, error = resource.exception.message)

                    Resource.Loading -> state.copy(isLoading = true, error = null)
                    is Resource.Success -> state.copy(isLoading = false, error = null, categories = resource.data)
                }

            }
        }
    }

    private fun getExpenseCategories() {
        viewModelScope.launch {
            repository.getExpenseCategories().collectLatest { resource ->
                state = when (resource) {
                    is Resource.Error -> state.copy(isLoading = false, error = resource.exception.message)

                    Resource.Loading -> state.copy(isLoading = true, error = null)
                    is Resource.Success -> state.copy(isLoading = false, error = null, categories = resource.data)
                }

            }
        }
    }

    private fun updateIncome() {
        viewModelScope.launch {
            val flow=repository.upsertIncome(
                Income(
                    id = state.id!!,
                    name = state.name!!,
                    amount = state.amount!!.toDouble(),
                    categoryId = state.category!!.id,
                    date = state.date!!,
                    description = state.description!!,
                )
            )
            flow.collectLatest { resource ->
                state = when (resource) {
                    is Resource.Error -> {
                        _eventFlow.emit(UIEvent.ShowSnackbar(resource.exception.message ?: "error"))
                        state.copy(
                            isLoading = false,
                            error = resource.exception.message
                        )
                    }

                    Resource.Loading -> state.copy(isLoading = true, error = null)
                    is Resource.Success -> {
                        Log.d(TAG, "updateIncome: success")
                        _eventFlow.emit(UIEvent.Updated)
                        _eventFlow.emit(UIEvent.ShowSnackbar("submitted successfully"))
                        EditState()
                    }
                }

            }
        }
    }

    private fun updateExpense(){
        viewModelScope.launch {
           val flow= repository.upsertExpense(
                Expense(
                    id = state.id!!,
                    name = state.name!!,
                    amount = (state.amount!!.toDouble()).times(-1),
                    categoryId = state.category!!.id,
                    date = state.date!!,
                    description = state.description!!,
                )
            )
            flow.collectLatest { resource ->
                state = when (resource) {
                    is Resource.Error -> {
                        _eventFlow.emit(UIEvent.ShowSnackbar(resource.exception.message ?: "error"))
                        state.copy(
                            isLoading = false,
                            error = resource.exception.message
                        )
                    }

                    Resource.Loading -> state.copy(isLoading = true, error = null)
                    is Resource.Success -> {
                        Log.d(TAG, "updateExpense: success")
                        _eventFlow.emit(UIEvent.Updated)
                        _eventFlow.emit(UIEvent.ShowSnackbar("submitted successfully"))
                        EditState()
                    }
                }

            }
        }
    }

    private fun getIncomeAndCategory(id: Int) {
        repository.getIncomeAndCategoryById(id).onEach { resource ->
            when (resource) {
                is Resource.Error -> {
                    state = state.copy(isLoading = false, error = resource.exception.message)
                    _eventFlow.emit(UIEvent.ShowSnackbar(state.error ?: "error"))
                }

                Resource.Loading -> {
                    state = state.copy(isLoading = true, error = null)
                }
                is Resource.Success -> {
                    val income = resource.data.income
                    val category = resource.data.category
                    state = state.copy(
                        isLoading = false,
                        error = null,
                        name = income.name,
                        amount = income.amount.toString(),
                        category = category,
                        date = income.date,
                        description = income.description

                        )
                    _eventFlow.emit(UIEvent.ShowSnackbar("Updated successfully"))
                }
            }

        }.launchIn(viewModelScope)
    }

    private fun getExpenseAndCategory(id: Int) {
        repository.getExpenseAndCategoryById(id).onEach { resource ->
            Log.d(TAG, "getExpenseAndCategory: $resource")
            when (resource) {
                is Resource.Error -> {
                    state = state.copy(isLoading = false, error = resource.exception.message)
                    _eventFlow.emit(UIEvent.ShowSnackbar(state.error ?: "error"))
                }
                Resource.Loading -> {
                    state = state.copy(isLoading = true, error = null)
                }
                is Resource.Success -> {
                    val expense = resource.data.expense
                    val category = resource.data.category
                    state = state.copy(
                        isLoading = false,
                        error = null,
                        name = expense.name,
                        amount = expense.amount.times(-1).toString(),
                        category = category,
                        date = expense.date,
                        description = expense.description

                        )
                    Log.d(TAG, "getExpenseAndCategory: $state")
                }
            }

        }.launchIn(viewModelScope)
    }
    companion object{
        private const val TAG = "EditViewModel"
    }

    sealed class UIEvent {
        data class ShowSnackbar(val message: String) : UIEvent()
        data object Updated : UIEvent()
    }

}

data class EditState(
    val isLoading: Boolean = false,
    val error: String? = null,
   // val transaction: Transaction? = null,
    val isExpense:Boolean?=null,
    val id: Int?=null,
    val name: String? = null,
    val amount: String? = null,
    val category: Category? = null,
    val date: Long? = null,
    val description:String?=null,
    val categories:List<Category> = emptyList(),
)

sealed class EditEvent {
    data class OnNameChange(val name: String?) : EditEvent()
    data class OnAmountChange(val amount: String?) : EditEvent()
    data class OnCategoryChange(val category: Category?) : EditEvent()
    data class OnDateChange(val date: Long?) : EditEvent()
    data class OnDescriptionChange(val description:String?) : EditEvent()
    data object OnUpdate:EditEvent()
}