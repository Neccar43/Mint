package com.novacodestudios.mint.presentation.expense

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Query
import com.novacodestudios.mint.data.local.entitiy.Expense
import com.novacodestudios.mint.data.repository.MintRepository
import com.novacodestudios.mint.model.GroupedTransactions
import com.novacodestudios.mint.model.Transaction
import com.novacodestudios.mint.model.TransactionPeriod
import com.novacodestudios.mint.util.Resource
import com.novacodestudios.mint.util.groupByMonth
import com.novacodestudios.mint.util.groupByToday
import com.novacodestudios.mint.util.groupByWeek
import com.novacodestudios.mint.util.groupByYearMonth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ExpenseViewModel @Inject constructor(
    private val repository: MintRepository,
) : ViewModel() {
    var state by mutableStateOf(ExpenseState())
        private set

    private val _eventFlow=MutableSharedFlow<String>()
    val eventFlow=_eventFlow.asSharedFlow()

    init {
        getTransaction()
    }

    private fun getTransaction() {
        viewModelScope.launch {
            repository.getCombinedTransaction().collectLatest { resource ->
                when (resource) {
                    is Resource.Error -> {
                        Log.e(TAG, "getTransaction: error: ${resource.exception}")
                        state = state.copy(
                            error = resource.exception.message,
                            isLoading = false
                        )
                    }

                    Resource.Loading -> {
                        Log.d(TAG, "getTransaction: loading")
                        state = state.copy(isLoading = true, error = null)
                    }

                    is Resource.Success -> {
                        state = state.copy(
                            transactions = resource.data,
                            isLoading = false,
                            error = null,
                            groupedTransactions = getSortedAndGroupedTransactions(
                                resource.data,
                                state.transactionPeriod
                            )
                        )
                        Log.d(TAG, "getTransaction: success : ${state.transactions}")
                        calculateBalance()
                    }
                }
            }
            Log.d(TAG, "getTransaction: state:${state.groupedTransactions}")
        }
    }

    private fun calculateBalance() {
        val transactions = state.groupedTransactions.flatMap { it.transactions }
        val balance = transactions.sumOf { it.amount }
        state = state.copy(balance = balance)
        Log.d(TAG, "getBalance: ${state.balance}")
    }

    fun onEvent(event: ExpenseEvent) {
        when (event) {
            is ExpenseEvent.OnTransactionPeriodChange -> {
                state = state.copy(
                    transactionPeriod = event.period,
                    groupedTransactions = getSortedAndGroupedTransactions(
                        state.transactions,
                        event.period
                    ),
                )
                calculateBalance()
            }

            is ExpenseEvent.OnSearchChanged -> search(event.query)
            is ExpenseEvent.OnDeleteTransaction -> deleteTransaction(event.isExpense,event.id)
        }
    }

    private fun deleteExpense(id:Int) {
        viewModelScope.launch {
            repository.deleteExpenseById(id).collectLatest {resource->
                when (resource) {
                    is Resource.Error -> {
                        state = state.copy(isLoading = false, error = resource.exception.message)
                        _eventFlow.emit(state.error?:"error")
                    }
                    Resource.Loading -> {
                        state = state.copy(isLoading = true, error = null)}
                    is Resource.Success -> {
                        state = state.copy(isLoading = false, error = null)
                        _eventFlow.emit("Deleted successfully")
                    }
                }

            }
        }
    }

    private fun deleteTransaction(isExpense: Boolean,id: Int) {
        if (isExpense){
            deleteExpense(id)
            return
        }
        deleteIncome(id)
    }

    private fun deleteIncome(id: Int) {
        viewModelScope.launch {
            repository.deleteIncomeById(id).collectLatest {resource->
                when (resource) {
                    is Resource.Error -> {
                        state = state.copy(isLoading = false, error = resource.exception.message)
                        _eventFlow.emit(state.error?:"error")
                    }
                    Resource.Loading -> {
                        state = state.copy(isLoading = true, error = null)}
                    is Resource.Success -> {
                        state = state.copy(isLoading = false, error = null)
                        _eventFlow.emit("Deleted successfully")
                    }
                }

            }
        }
    }


    private fun getSortedAndGroupedTransactions(
        transactions: List<Transaction>,
        period: TransactionPeriod
    ): List<GroupedTransactions> {
        val sortedTransactions = when (period) {
            TransactionPeriod.DAY -> transactions.groupByToday()
            TransactionPeriod.WEEK -> transactions.groupByWeek(true)
            TransactionPeriod.MONTH -> transactions.groupByMonth()
            TransactionPeriod.YEAR -> transactions.groupByYearMonth()
        }

        return sortedTransactions
    }

    private fun searchTransactions(
        transactions: List<Transaction>,
        nameQuery: String? = null,
        categoryQuery: String? = null
    ): List<Transaction> {
        return transactions.filter { transaction ->
            val matchesName =
                nameQuery?.let { transaction.name.contains(it, ignoreCase = true) } ?: true
            val matchesCategory =
                categoryQuery?.let { transaction.category.contains(it, ignoreCase = true) } ?: true

            matchesName && matchesCategory
        }
    }

    private fun search(query: String?){
        state = state.copy(searchQuery = query)
        state = state.copy(searchTransactions = searchTransactions(state.transactions, nameQuery = state.searchQuery))
    }


    companion object {
        private const val TAG = "ExpenseViewModel"
    }

    /*sealed class UIState{
        data class ShowSnackBar(val message:String)
        data object
    }*/

}

// TODO: ayrı dosyalara taşı
sealed class ExpenseEvent {
    data class OnTransactionPeriodChange(val period: TransactionPeriod) : ExpenseEvent()
    data class OnSearchChanged(val query:String):ExpenseEvent()
    data class OnDeleteTransaction(val isExpense:Boolean,val id: Int):ExpenseEvent()
}

data class ExpenseState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val balance: Double = 0.0,
    val transactions: List<Transaction> = emptyList(),
    val groupedTransactions: List<GroupedTransactions> = emptyList(),
    val transactionPeriod: TransactionPeriod = TransactionPeriod.WEEK,
    val searchQuery:String?=null,
    val searchTransactions:List<Transaction> = emptyList(),
)