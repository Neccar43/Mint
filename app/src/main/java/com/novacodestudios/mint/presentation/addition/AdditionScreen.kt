package com.novacodestudios.mint.presentation.addition

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import com.novacodestudios.mint.model.Category
import com.novacodestudios.mint.ui.theme.MintTheme
import com.novacodestudios.mint.util.toFormattedDate
import kotlinx.coroutines.flow.collectLatest
import java.util.Calendar

@Composable
fun AdditionScreen(
    viewModel: AdditionViewModel = hiltViewModel(),
    onNavigateExpenseScreen: () -> Unit,

    ) {
    val snackbarHostState =
        remember { SnackbarHostState() }

    val context = LocalContext.current
    LaunchedEffect(key1 = true) {
        viewModel.eventFlow.collectLatest { event ->
            when (event) {
                is AdditionViewModel.UIEvent.ShowSnackbar -> snackbarHostState.showSnackbar(event.message)
                AdditionViewModel.UIEvent.Submitted -> {
                    //snackbarHostState.showSnackbar(event)
                }
            }

        }
    }

    AdditionScreenContent(
        state = viewModel.state,
        snackbarHostState = snackbarHostState,
        onEvent = viewModel::onEvent
    )

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdditionScreenContent(
    state: AdditionState,
    snackbarHostState: SnackbarHostState,
    onEvent: (AdditionEvent) -> Unit
) {
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = { AdditionTopBar() }
    ) { paddingValues ->
        var isDialogVisible by remember { mutableStateOf(false) }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            TabRow(selectedTabIndex = if (state.isExpense) 0 else 1) {
                Tab(
                    selected = state.isExpense,
                    onClick = { onEvent(AdditionEvent.OnTransactionTypeChange(isExpense = true)) },
                    text = { Text(text = "Expense") })

                Tab(
                    selected = !state.isExpense,
                    onClick = { onEvent(AdditionEvent.OnTransactionTypeChange(isExpense = false)) },
                    text = { Text(text = "Income") })
            }
            Column(
                modifier = Modifier
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Card(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Amount")
                        MintTextField(
                            modifier = Modifier,
                            value = state.amount ?:"",
                            onValueChange = {
                                onEvent(AdditionEvent.OnAmountChange(it))
                            },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true
                        )

                    }
                    HorizontalDivider()

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Name")
                        MintTextField(
                            value = state.name ?: "",
                            onValueChange = { onEvent(AdditionEvent.OnNameChange(it)) }, singleLine = true)
                    }
                    HorizontalDivider()
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Date")
                        ElevatedCard(
                            shape = RoundedCornerShape(20),
                            modifier = Modifier,
                            onClick = { isDialogVisible = true }) {
                            Text(
                                text = state.date.toFormattedDate(),
                                modifier = Modifier
                                    .padding(horizontal = 4.dp, vertical = 2.dp)
                            )
                        }
                    }
                    HorizontalDivider()

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Description")
                        MintTextField(
                            value = state.description ?: "",
                            onValueChange = { onEvent(AdditionEvent.OnDescriptionChange(it))}, singleLine = true)
                    }
                    HorizontalDivider()

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Category")
                        CategoryMenu(
                            selectedCategory = state.category,
                            onCategoryChange = { onEvent(AdditionEvent.OnCategoryChange(it)) },
                            categories = state.categories,
                        )
                    }

                }
                Button(
                    onClick = { onEvent(AdditionEvent.OnSubmit) },
                    Modifier
                        .size(width = 150.dp, height = 55.dp)
                        //.width(150.dp)
                        .padding(top = 8.dp),
                    // shape = RoundedCornerShape(20)
                ) {
                    Text(text = "Submit")
                }
            }
        }
        val datePickerState = rememberDatePickerState()

        if (isDialogVisible) {
            MintDatePickerDialog(
                datePickerState = datePickerState,
                onDismiss = { isDialogVisible = false },
                onConfirm = {
                    onEvent(AdditionEvent.OnDateChange(date = datePickerState.selectedDateMillis?:0))
                    isDialogVisible=false
                })
        }


    }
}


@Composable
fun CategoryMenu(
    modifier: Modifier = Modifier,
    selectedCategory: Category?,
    onCategoryChange: (Category) -> Unit,
    categories: List<Category>
) {
    var expanded by remember { mutableStateOf(false) }
    Column(
        modifier = Modifier.clickable { expanded=!expanded },
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                modifier = Modifier.padding(8.dp),
                text = selectedCategory?.name ?: "Category",
                fontSize = MaterialTheme.typography.titleMedium.fontSize,
            )
            Icon(
                imageVector = if (!expanded) Icons.Default.ArrowDropDown else Icons.Default.ArrowDropUp,
                contentDescription = ""
            )

        }


        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }) {
            categories.forEach { category ->
                DropdownMenuItem(
                    text = { Text(text = category.name) },
                    onClick = {
                        onCategoryChange(category)
                        expanded = false
                    })
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdditionTopBar() {
    TopAppBar(title = { Text(text = "Add") })
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
private fun AdditionScreenPreview() {
    MintTheme {

        AdditionScreenContent(
            state = AdditionState(),
            snackbarHostState = SnackbarHostState(),
            onEvent = {

            })


    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MintDatePickerDialog(
    //modifier: Modifier = Modifier,
    datePickerState: DatePickerState,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = { onConfirm() }) {
                Text(text = "OK")
            }
        },
        dismissButton = {
            TextButton(onClick = { onDismiss() }) {
                Text(text = "Cancel")
            }
        }
    ) {
        DatePicker(state = datePickerState)
    }


}

@Composable
fun MintTextField(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    singleLine: Boolean = false,
    maxLines: Int = if (singleLine) 1 else Int.MAX_VALUE,
    minLines: Int = 1,
) {
    val textStyle: TextStyle = LocalTextStyle.current
    val textColor = textStyle.color
    val mergedTextStyle = textStyle.merge(TextStyle(color = textColor, textAlign = TextAlign.Right))
    BasicTextField(
        modifier = modifier,
        value = value,
        onValueChange = onValueChange,
        keyboardOptions = keyboardOptions,
        textStyle = mergedTextStyle,
        singleLine = singleLine,
        maxLines = maxLines,
        minLines = minLines,
    )
}
