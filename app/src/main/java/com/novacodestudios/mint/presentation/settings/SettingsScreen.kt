package com.novacodestudios.mint.presentation.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import com.novacodestudios.mint.ui.theme.MintTheme
import kotlinx.coroutines.flow.collectLatest

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel(),
    onNavigateCategoryScreen: () -> Unit,

    ) {
    val snackbarHostState =
        remember { SnackbarHostState() }

    val context = LocalContext.current
    LaunchedEffect(key1 = true) {
        viewModel.eventFlow.collectLatest { message ->
            snackbarHostState.showSnackbar(message)
        }
    }

    SettingsScreenContent(
        state = viewModel.state,
        snackbarHostState = snackbarHostState,
        onEvent = viewModel::onEvent,
        onNavigateCategoryScreen = onNavigateCategoryScreen
    )

}

@Composable
fun SettingsScreenContent(
    state: SettingsState,
    snackbarHostState: SnackbarHostState,
    onEvent: (SettingsEvent) -> Unit,
    onNavigateCategoryScreen: () -> Unit,
) {
    var isDialogVisible by remember { mutableStateOf(false) }
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = { SettingsTopBar() }
    ) { paddingValues ->
        Column(
            modifier = Modifier.padding(paddingValues),
        ) {
            Card(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onNavigateCategoryScreen() }
                        .padding(vertical = 8.dp)
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "Categories")
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = "",
                    )
                }
                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { isDialogVisible = true }
                        .padding(vertical = 10.dp, horizontal = 16.dp),
                    text = "Erase Data",
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
        if (isDialogVisible) {
            AlertDialog(onDismissRequest = { isDialogVisible = false }, confirmButton = {
                TextButton(
                    onClick = { onEvent(SettingsEvent.OnEraseData); isDialogVisible=false }) {
                    Text(text = "OK")
                }
            },
                title = { Text(text = "Confirm Erase Data") },
                text = { Text(text = "Are you sure you want to erase all data? This action is irreversible and all your data will be permanently deleted. Do you want to proceed?") },
                dismissButton = {
                    TextButton(onClick = { isDialogVisible = false }) {
                        Text(text = "Cancel")
                    }
                }

            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsTopBar() {
    TopAppBar(title = { Text(text = "Settings") })
}

@Preview(showBackground = true)
@Composable
private fun SettingsScreenPreview() {
    MintTheme {
        SettingsScreenContent(
            state = SettingsState(),
            snackbarHostState = SnackbarHostState(),
            onEvent = {},
            onNavigateCategoryScreen = {}
        )
    }

}