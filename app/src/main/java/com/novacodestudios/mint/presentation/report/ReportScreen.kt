package com.novacodestudios.mint.presentation.report

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.novacodestudios.mint.model.GroupedTransactions
import com.novacodestudios.mint.model.Transaction
import com.novacodestudios.mint.presentation.expense.TransactionList
import com.novacodestudios.mint.presentation.report.component.TransactionChart
import com.novacodestudios.mint.presentation.report.component.getDateForDay
import com.novacodestudios.mint.ui.theme.MintTheme
import com.novacodestudios.mint.util.groupByWeek
import java.util.Calendar

@Composable
fun ReportScreen(
    viewModel: ReportViewModel = hiltViewModel(),

    ) {
    val snackbarHostState =
        remember { SnackbarHostState() }

    /*val context = LocalContext.current
    LaunchedEffect(key1 = true) {
        viewModel.eventFlow.kotlinx.coroutines.flow.collectLatest { event ->
            when (event) {
                is ReportViewModel.UIEvent.ShowSnackbar -> handleUIText(event.message, context)
                // TODO: Add more events
            }

        }
    }*/

    ReportScreenContent(
        state = viewModel.state,
        snackbarHostState = snackbarHostState,
    )

}

@Composable
fun ReportScreenContent(
    state: ReportState,
    snackbarHostState: SnackbarHostState,
) {
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = { ReportTopBar() }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize(),
        ) {
            if (state.groupedTransactions.isNotEmpty()) {
                TransactionChart(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    gropedTransactions = state.groupedTransactions.map { groupedTransaction ->
                        GroupedTransactions(
                            header = groupedTransaction.header,
                            transactions = groupedTransaction.transactions.filter { transaction -> transaction.isExpense }
                        )
                    },
                    barWidth = 44.dp

                )
            }
            TransactionList(modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),groupedTransactions = state.groupedTransactions)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportTopBar() {
    TopAppBar(title = { Text(text = "Reports") })
}

@Preview(showBackground = true)
@Composable
private fun ReportScreenPreview() {
    MintTheme {
        ReportScreenContent(
            state = ReportState(
                groupedTransactions = listOf(
                    Transaction(
                        id = 1,
                        name = "Breakfast",
                        amount = 5.0,
                        isExpense = true,
                        category = "Food",
                        categoryColor = 0xFF5733,
                        date = getDateForDay(Calendar.MONDAY)
                    ),
                    Transaction(
                        id = 2,
                        name = "Bus Ticket",
                        amount = 2.5,
                        isExpense = true,
                        category = "Transport",
                        categoryColor = 0xC70039,
                        date = getDateForDay(Calendar.MONDAY)
                    ),
                    Transaction(
                        id = 3,
                        name = "Bus Ticket",
                        amount = 5.5,
                        isExpense = true,
                        category = "Transport",
                        categoryColor = 0xC70039,
                        date = getDateForDay(Calendar.MONDAY)
                    ),
                    Transaction(
                        id = 3,
                        name = "Lunch",
                        amount = 12.0,
                        isExpense = true,
                        category = "Food",
                        categoryColor = 0xFFC300,
                        date = getDateForDay(Calendar.TUESDAY)
                    ),
                    Transaction(
                        id = 4,
                        name = "Coffee",
                        amount = 3.5,
                        isExpense = true,
                        category = "Food",
                        categoryColor = 0xDAF7A6,
                        date = getDateForDay(Calendar.TUESDAY)
                    ),
                    Transaction(
                        id = 5,
                        name = "Groceries",
                        amount = 30.0,
                        isExpense = true,
                        category = "Groceries",
                        categoryColor = 0x581845,
                        date = getDateForDay(Calendar.WEDNESDAY)
                    ),
                    Transaction(
                        id = 6,
                        name = "Gym",
                        amount = 20.0,
                        isExpense = true,
                        category = "Health",
                        categoryColor = 0x900C3F,
                        date = getDateForDay(Calendar.WEDNESDAY)
                    ),
                    Transaction(
                        id = 7,
                        name = "Dinner",
                        amount = 25.0,
                        isExpense = true,
                        category = "Food",
                        categoryColor = 0xFF5733,
                        date = getDateForDay(Calendar.THURSDAY)
                    ),
                    Transaction(
                        id = 8,
                        name = "Taxi",
                        amount = 15.0,
                        isExpense = true,
                        category = "Transport",
                        categoryColor = 0xC70039,
                        date = getDateForDay(Calendar.THURSDAY)
                    ),
                    Transaction(
                        id = 9,
                        name = "Cinema",
                        amount = 30.0,
                        isExpense = true,
                        category = "Entertainment",
                        categoryColor = 0xFFC300,
                        date = getDateForDay(Calendar.FRIDAY)
                    ),
                    Transaction(
                        id = 10,
                        name = "Drinks",
                        amount = 20.0,
                        isExpense = true,
                        category = "Entertainment",
                        categoryColor = 0xDAF7A6,
                        date = getDateForDay(Calendar.FRIDAY)
                    ),
                    Transaction(
                        id = 11,
                        name = "Shopping",
                        amount = 50.0,
                        isExpense = true,
                        category = "Shopping",
                        categoryColor = Color.Red.toArgb(),
                        date = getDateForDay(Calendar.SATURDAY)
                    ),
                    Transaction(
                        id = 12,
                        name = "Dinner",
                        amount = 30.0,
                        isExpense = true,
                        category = "Food",
                        categoryColor = 0x900C3F,
                        date = getDateForDay(Calendar.SATURDAY)
                    ),
                    Transaction(
                        id = 13,
                        name = "Brunch",
                        amount = 25.0,
                        isExpense = true,
                        category = "Food",
                        categoryColor = Color.Green.toArgb(),
                        date = getDateForDay(Calendar.SUNDAY)
                    ),
                    Transaction(
                        id = 14,
                        name = "Park",
                        amount = 15.0,
                        isExpense = true,
                        category = "Entertainment",
                        categoryColor = 0xC70039,
                        date = getDateForDay(Calendar.SUNDAY)
                    )

                ).groupByWeek()
            ),
            snackbarHostState = SnackbarHostState()
        )
    }

}