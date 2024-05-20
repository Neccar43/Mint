package com.novacodestudios.mint.presentation.edit

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.novacodestudios.mint.presentation.addition.CategoryMenu
import com.novacodestudios.mint.presentation.addition.MintDatePickerDialog
import com.novacodestudios.mint.presentation.addition.MintTextField
import com.novacodestudios.mint.ui.theme.MintTheme
import com.novacodestudios.mint.util.toFormattedDate
import kotlinx.coroutines.flow.collectLatest

@Composable
fun EditScreen(
    viewModel: EditViewModel = hiltViewModel(),
    onNavigateExpenseScreen: () -> Unit

) {
    val snackbarHostState =
        remember { SnackbarHostState() }

    LaunchedEffect(key1 = true) {
        viewModel.eventFlow.collectLatest { event ->
            when (event) {
                is EditViewModel.UIEvent.ShowSnackbar -> {
                    snackbarHostState.showSnackbar(event.message)
                }

                EditViewModel.UIEvent.Updated -> {
                    onNavigateExpenseScreen()
                }
            }

        }
    }

    EditScreenContent(
        state = viewModel.state,
        snackbarHostState = snackbarHostState,
        onNavigateExpenseScreen = onNavigateExpenseScreen,
        onEvent = viewModel::onEvent

    )

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditScreenContent(
    state: EditState,
    snackbarHostState: SnackbarHostState,
    onNavigateExpenseScreen: () -> Unit,
    onEvent: (EditEvent) -> Unit,
) {
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = { EditTopBar() }
    ) { paddingValues ->
        var isDialogVisible by remember { mutableStateOf(false) }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
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
                        value = state.amount ?: "",
                        onValueChange = {
                            onEvent(EditEvent.OnAmountChange(it))
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
                        onValueChange = { onEvent(EditEvent.OnNameChange(it)) }, singleLine = true
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
                    Text(text = "Date")
                    ElevatedCard(
                        shape = RoundedCornerShape(20),
                        modifier = Modifier,
                        onClick = { isDialogVisible = true }) {
                        Text(
                            text = state.date?.toFormattedDate()?:"",
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
                        onValueChange = { onEvent(EditEvent.OnDescriptionChange(it)) },
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
                    Text(text = "Category")
                    CategoryMenu(
                        selectedCategory = state.category,
                        onCategoryChange = { onEvent(EditEvent.OnCategoryChange(it)) },
                        categories = state.categories,
                    )
                }

            }
            Row(horizontalArrangement = Arrangement.SpaceAround, modifier = Modifier.fillMaxWidth()) {
                OutlinedButton(
                    onClick = { onNavigateExpenseScreen() },
                    Modifier
                        .size(width = 150.dp, height = 55.dp)
                        //.width(150.dp)
                        .padding(top = 8.dp),
                ) {
                    Text(text = "Cancel")
                }
                Button(
                    onClick = { onEvent(EditEvent.OnUpdate) },
                    Modifier
                        .size(width = 150.dp, height = 55.dp)
                        //.width(150.dp)
                        .padding(top = 8.dp),
                ) {
                    Text(text = "Update")
                }
            }


        }
        val datePickerState = rememberDatePickerState()

        if (isDialogVisible) {
            MintDatePickerDialog(
                datePickerState = datePickerState,
                onDismiss = { isDialogVisible = false },
                onConfirm = {
                    isDialogVisible = false
                    onEvent(EditEvent.OnDateChange(date = datePickerState.selectedDateMillis ?: 0))
                })
        }


    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditTopBar() {
    TopAppBar(title = { Text(text = "Edit")})
}

@Preview(showBackground = true)
@Composable
private fun EditScreenPreview() {
    MintTheme {
        EditScreenContent(
            state = EditState(),
            snackbarHostState = SnackbarHostState(),
            onNavigateExpenseScreen = {},
            onEvent = {}
        )
    }

}