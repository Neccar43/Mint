package com.novacodestudios.mint.presentation.report

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReportViewModel @Inject constructor(
    private val repository: MintRepository,
) : ViewModel() {
    var state by mutableStateOf(ReportState())
        private set

    init {
        getTransaction()
    }

    fun onEvent(event: ReportEvent) {
        when (event) {
            is ReportEvent.OnTransactionPeriodChange -> {
                state = state.copy(
                    transactionPeriod = event.period,
                    groupedTransactions = getSortedAndGroupedTransactions(
                        state.transactions,
                        event.period
                    )
                )
            }
        }
    }

    private fun getSortedAndGroupedTransactions(
        transactions: List<Transaction>,
        period: TransactionPeriod
    ): List<GroupedTransactions> {
        val sortedTransactions = when (period) {
            TransactionPeriod.DAY -> transactions.groupByToday()
            TransactionPeriod.WEEK -> transactions.groupByWeek()
            TransactionPeriod.MONTH -> transactions.groupByMonth()
            TransactionPeriod.YEAR -> transactions.groupByYearMonth()
        }

        return sortedTransactions
    }

    private fun getTransaction() {
        viewModelScope.launch {
            repository.getCombinedTransaction().collectLatest { resource ->
                state = when (resource) {
                    is Resource.Error -> state.copy(
                        error = resource.exception.message,
                        isLoading = false
                    )

                    Resource.Loading -> state.copy(isLoading = true, error = null)
                    is Resource.Success -> {
                        state.copy(
                            transactions = resource.data,
                            groupedTransactions = getSortedAndGroupedTransactions(
                                resource.data,
                                state.transactionPeriod
                            ),
                            isLoading = false,
                            error = null
                        )
                    }
                }
            }
        }
    }

}

data class ReportState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val transactions: List<Transaction> = emptyList(),
    val groupedTransactions: List<GroupedTransactions> = emptyList(),
    val transactionPeriod: TransactionPeriod = TransactionPeriod.WEEK,
)

sealed class ReportEvent {
    data class OnTransactionPeriodChange(val period: TransactionPeriod) : ReportEvent()
}