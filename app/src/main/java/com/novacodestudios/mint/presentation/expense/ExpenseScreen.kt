package com.novacodestudios.mint.presentation.expense

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusModifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.core.graphics.ColorUtils
import androidx.hilt.navigation.compose.hiltViewModel
import com.novacodestudios.mint.model.GroupedTransactions
import com.novacodestudios.mint.model.Transaction
import com.novacodestudios.mint.model.TransactionData
import com.novacodestudios.mint.model.TransactionPeriod
import com.novacodestudios.mint.presentation.edit.EditViewModel
import kotlinx.coroutines.flow.collectLatest
import java.util.Calendar
import java.util.Locale


@Composable
fun ExpenseScreen(
    viewModel: ExpenseViewModel = hiltViewModel(),
    onNavigateEditScreen: (Boolean, Int) -> Unit
) {


    val snackbarHostState =
        remember { SnackbarHostState() }

    // val context = LocalContext.current
    LaunchedEffect(key1 = true) {
        viewModel.eventFlow.collectLatest { message ->
            snackbarHostState.showSnackbar(message)
        }
    }

    ExpenseScreenContent(
        state = viewModel.state,
        snackbarHostState = snackbarHostState,
        onEvent = viewModel::onEvent,
        onNavigateEditScreen = onNavigateEditScreen
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpenseScreenContent(
    state: ExpenseState,
    snackbarHostState: SnackbarHostState,
    onEvent: (ExpenseEvent) -> Unit,
    onNavigateEditScreen: (Boolean, Int) -> Unit,
) {
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            ExpenseTopBar()
        }
    ) { paddingValues ->

        var transactionData by remember {
            mutableStateOf(TransactionData())
        }
        var isVisible by remember {
            mutableStateOf(false)
        }
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            MintSearchBar(
                modifier = Modifier,
                query = state.searchQuery ?: "",
                onSearch = { onEvent(ExpenseEvent.OnSearchChanged(it)) }
            ) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp)
                ) {
                    items(state.searchTransactions) { trasaction ->
                        TransactionListItem(
                            transaction = trasaction,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }
                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(text = "Total for:", fontSize = MaterialTheme.typography.titleMedium.fontSize)
                PeriodMenu(
                    selectedPeriodName = state.transactionPeriod.name,
                    onPeriodChange = { onEvent(ExpenseEvent.OnTransactionPeriodChange(it)) }
                )
            }
            Text(
                modifier = Modifier.padding(vertical = 16.dp),
                text = "$ " + state.balance.toString(),
                fontSize = MaterialTheme.typography.displaySmall.fontSize
            )
            TransactionList(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp), groupedTransactions = state.groupedTransactions,
                onLongClick = { transaction ->
                    isVisible = true
                    transactionData =
                        transactionData.copy(isExpense = transaction.isExpense, id = transaction.id)
                }
            )
        }


        val sheetState = rememberModalBottomSheetState()
        if (isVisible) {
            ModalBottomSheet(onDismissRequest = { isVisible = false }, sheetState = sheetState) {
                SheetItem(text = "Edit",
                    onClick = {
                        onNavigateEditScreen(transactionData.isExpense!!, transactionData.id!!)
                    })
                SheetItem(
                    text = "Delete",
                    onClick = {
                        onEvent(
                            ExpenseEvent.OnDeleteTransaction(
                                isExpense = transactionData.isExpense!!, id = transactionData.id!!
                            )
                        )
                        isVisible = false
                    })
            }
        }

    }
}

@Composable
fun SheetItem(modifier: Modifier = Modifier, text: String, onClick: () -> Unit) {
    Text(
        modifier = modifier
            .clickable { onClick() }
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 10.dp),
        text = text,
        fontSize = MaterialTheme.typography.bodyLarge.fontSize
    )
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TransactionList(
    modifier: Modifier = Modifier,
    onLongClick: (Transaction) -> Unit = {},
    groupedTransactions: List<GroupedTransactions>
) {
    LazyColumn(modifier = modifier) {
        groupedTransactions.forEach { groupedTransaction ->
            item {
                Text(
                    text = groupedTransaction.header,
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.Gray,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                )
                HorizontalDivider()
            }

            items(groupedTransaction.transactions) { transaction ->
                TransactionListItem(
                    transaction = transaction, modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .combinedClickable(onLongClick = {
                            onLongClick(transaction)
                        }) {}
                )
            }
            item {
                HorizontalDivider()
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                ) {
                    Text(
                        text = "Total:",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.Gray,
                        modifier = Modifier
                    )
                    Text(
                        text = groupedTransaction.transactions.sumOf {
                            it.amount
                        }.toString() + " $",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.Gray,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                    )

                }

            }
        }
    }
}


@Composable
fun TransactionListItem(modifier: Modifier = Modifier, transaction: Transaction) {
    Column(modifier = modifier) {
        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
            Text(
                text = transaction.name,
                fontSize = MaterialTheme.typography.bodyLarge.fontSize,
                fontWeight = FontWeight.Bold,

                )
            Text(
                text = transaction.amount.toString() + " $",
                fontSize = MaterialTheme.typography.bodyLarge.fontSize,
                fontWeight = FontWeight.Bold
            )
        }
        Row(
            horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp)
        ) {
            CategoryItem(name = transaction.category, color = transaction.categoryColor)
            Text(text = "16:24") // TODO: aktif saati al

        }
    }
}

@Composable
fun CategoryItem(name: String, color: Int) {
    val lightColor = Color(ColorUtils.blendARGB(color, 0xFFFFFFFF.toInt(), 0.7f))
    Text(
        text = name,
        modifier = Modifier
            .background(color = Color(color), shape = RoundedCornerShape(30))
            .padding(horizontal = 2.dp),
        color = lightColor,
        fontSize = MaterialTheme.typography.labelLarge.fontSize

    )
}

@Composable
fun PeriodMenu(
    modifier: Modifier = Modifier,
    selectedPeriodName: String,
    onPeriodChange: (TransactionPeriod) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    Column(
        modifier = Modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        OutlinedCard(
            onClick = { expanded = true },
            modifier = Modifier
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    text = "this " + selectedPeriodName.lowercase(Locale.ROOT),
                    fontSize = MaterialTheme.typography.titleMedium.fontSize,
                )
                Icon(
                    imageVector = if (!expanded) Icons.Default.ArrowDropDown else Icons.Default.ArrowDropUp,
                    contentDescription = ""
                )

            }

        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }) {
            TransactionPeriod.entries.forEach { period ->
                DropdownMenuItem(
                    text = { Text(text = period.name) },
                    onClick = {
                        onPeriodChange(period)
                        expanded = false
                    })
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MintSearchBar(
    modifier: Modifier = Modifier,
    query: String,
    onSearch: (String) -> Unit,
    content: @Composable ColumnScope.() -> Unit,
) {
    var isActive by remember {
        mutableStateOf(false)
    }
    SearchBar(
        modifier = modifier,
        query = query,
        onQueryChange = { onSearch(it) },
        onSearch = { onSearch(it) },
        active = isActive,
        onActiveChange = { isActive = it },
        placeholder = { Text(text = "Search") },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = "") },
        content = content
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpenseTopBar() {

    TopAppBar(
        title = {
            Text(text = "Expense")
        },
    )


}

@Preview(showBackground = true)
@Composable
fun ExpenseScreenPreview() {
    ExpenseScreenContent(
        state = ExpenseState(
            transactions = listOf(
                Transaction(
                    id = 1,
                    name = "Alışveriş",
                    amount = -50.0,
                    isExpense = true,
                    category = "Market",
                    categoryColor = Color.Red.toArgb(),
                    date = Calendar.getInstance()
                        .apply { add(Calendar.DAY_OF_MONTH, -1) }.timeInMillis // Dün
                ),
                Transaction(
                    id = 1,
                    name = "Bağış",
                    amount = 70.0,
                    isExpense = false,
                    category = "Market",
                    categoryColor = Color.Red.toArgb(),
                    date = Calendar.getInstance()
                        .apply { add(Calendar.DAY_OF_MONTH, -1) }.timeInMillis // Dün
                ),
                Transaction(
                    id = 2,
                    name = "Maaş",
                    amount = 3000.0,
                    isExpense = false,
                    category = "Maaş",
                    categoryColor = Color.Green.toArgb(),
                    date = Calendar.getInstance().timeInMillis // Bugün
                ),
                Transaction(
                    id = 2,
                    name = "Market",
                    amount = 100.0,
                    isExpense = true,
                    category = "Market",
                    categoryColor = Color.Red.toArgb(),
                    date = Calendar.getInstance().timeInMillis // Bugün
                ),
                Transaction(
                    id = 2,
                    name = "Pizza",
                    amount = 50.0,
                    isExpense = true,
                    category = "Yemek",
                    categoryColor = Color.Magenta.toArgb(),
                    date = Calendar.getInstance().timeInMillis // Bugün
                ),
                Transaction(
                    id = 3,
                    name = "Akaryakıt",
                    amount = -150.0,
                    isExpense = true,
                    category = "Yakıt",
                    categoryColor = Color.Blue.toArgb(),
                    date = Calendar.getInstance()
                        .apply { add(Calendar.DAY_OF_MONTH, -3) }.timeInMillis // 3 gün önce
                ),
                Transaction(
                    id = 4,
                    name = "Restoran",
                    amount = -80.0,
                    isExpense = true,
                    category = "Yeme-İçme",
                    categoryColor = Color.Cyan.toArgb(),
                    date = Calendar.getInstance()
                        .apply { add(Calendar.DAY_OF_MONTH, -5) }.timeInMillis // 5 gün önce
                ),
                Transaction(
                    id = 1,
                    name = "Alışveriş",
                    amount = -50.0,
                    isExpense = true,
                    category = "Market",
                    categoryColor = Color.Red.toArgb(),
                    date = Calendar.getInstance().apply { add(Calendar.MONTH, -1) }.timeInMillis
                )
            ), transactionPeriod = TransactionPeriod.DAY
        ),
        onEvent = {},
        onNavigateEditScreen = { _, _ ->

        },
        snackbarHostState = SnackbarHostState()
    )
}