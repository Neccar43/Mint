package com.novacodestudios.mint.presentation.addition

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.novacodestudios.mint.data.local.entitiy.Expense
import com.novacodestudios.mint.data.local.entitiy.Income
import com.novacodestudios.mint.data.repository.MintRepository
import com.novacodestudios.mint.model.Category
import com.novacodestudios.mint.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class AdditionViewModel @Inject constructor(
    private val repository: MintRepository
) : ViewModel() {
    private val _eventFlow = MutableSharedFlow<UIEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    var state by mutableStateOf(AdditionState())
        private set

    init {
        getCategories()
    }

    fun onEvent(event: AdditionEvent) {
        when (event) {
            AdditionEvent.OnSubmit -> submit()
            is AdditionEvent.OnTransactionTypeChange -> {
                state = state.copy(
                    isExpense = event.isExpense,
                    isLoading = false,
                    error = null,
                    name = null,
                    amount = null,
                    date = Date().time,
                    description = null,
                    category = null,
                    categories = emptyList(),
                )
                getCategories()
            }

            is AdditionEvent.OnAmountChange -> state = state.copy(amount = event.amount)
            is AdditionEvent.OnCategoryChange -> {
                state = state.copy(category = event.category)
                getCategories()
            }
            is AdditionEvent.OnDateChange -> state = state.copy(date = event.date)
            is AdditionEvent.OnDescriptionChange -> state =
                state.copy(description = event.description)

            is AdditionEvent.OnNameChange -> state = state.copy(name = event.name)
        }
    }

    private fun getCategories() {
        if (state.isExpense) {
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


    private fun submitExpense() {
        viewModelScope.launch {
            val flow = repository.upsertExpense(
                Expense(
                    name = state.name!!,
                    amount = (state.amount!!.toDouble()).times(-1),
                    categoryId = state.category!!.id,
                    date = state.date,
                    description = state.description!!,
                )
            )
            flow.collectLatest { resource ->
                state = when (resource) {
                    is Resource.Error -> {
                        Log.e(TAG, "submitExpense: error ${resource.exception}")
                        _eventFlow.emit(UIEvent.ShowSnackbar(resource.exception.message ?: "error"))
                        state.copy(
                            isLoading = false,
                            error = resource.exception.message
                        )
                    }

                    Resource.Loading -> state.copy(isLoading = true, error = null)
                    is Resource.Success -> {
                        Log.d(TAG, "submitExpense: success")
                        _eventFlow.emit(UIEvent.Submitted)
                        _eventFlow.emit(UIEvent.ShowSnackbar("submitted successfully"))
                        state.copy(
                            isLoading = false,
                            error = null,
                            name = null,
                            amount = null,
                            date = Date().time,
                            description = null,
                            category = null
                        )
                    }
                }

            }
        }
    }

    private fun submitIncome() {
        viewModelScope.launch {
            val flow = repository.upsertIncome(
                Income(
                    name = state.name!!,
                    amount = state.amount!!.toDouble(),
                    categoryId = state.category!!.id,
                    date = state.date,
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
                        _eventFlow.emit(UIEvent.ShowSnackbar("submitted successfully"))
                        _eventFlow.emit(UIEvent.Submitted)
                        state.copy(
                            isLoading = false,
                            error = null,
                            name = null,
                            amount = null,
                            date = Date().time,
                            description = null,
                            category = null
                        )
                    }
                }

            }
        }
    }

    private fun submit() {
        state = state.copy(isLoading = true)
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
                _eventFlow.emit(UIEvent.ShowSnackbar(state.error ?: "error"))
                return@launch
            }
            if (state.isExpense) {
                submitExpense()
            } else {
                submitIncome()
            }
        }


    }
    companion object{
        private const val TAG = "AdditionViewModel"
    }


    sealed class UIEvent {
        data class ShowSnackbar(val message: String) : UIEvent()
        data object Submitted : UIEvent()
    }

}

data class AdditionState(
    val isLoading: Boolean = false,
    val isExpense: Boolean = true,
    val error: String? = null, // TODO: her Alan için ayrı hata bilgisini tut
    val name: String? = null,
    val amount: String? = null,
    //val recurrence
    val date: Long = Date().time,
    val description: String? = null,
    val category: Category? = null,
    val categories: List<Category> = emptyList(),
)

sealed class AdditionEvent {
    data object OnSubmit : AdditionEvent()
    data class OnTransactionTypeChange(val isExpense: Boolean) : AdditionEvent()
    data class OnAmountChange(val amount: String?) : AdditionEvent()
    data class OnNameChange(val name: String?) : AdditionEvent()
    data class OnDateChange(val date: Long) : AdditionEvent()
    data class OnDescriptionChange(val description: String?) : AdditionEvent()
    data class OnCategoryChange(val category: Category?) : AdditionEvent()
}