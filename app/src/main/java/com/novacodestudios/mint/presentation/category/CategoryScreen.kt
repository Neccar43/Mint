package com.novacodestudios.mint.presentation.category

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.DismissDirection
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.DismissState
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.DismissValue
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.Icon
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.SwipeToDismiss
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.rememberDismissState
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.novacodestudios.mint.data.local.entitiy.ExpenseCategory
import com.novacodestudios.mint.model.Category
import com.novacodestudios.mint.ui.theme.MintTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@Composable
fun CategoryScreen(
    viewModel: CategoryViewModel = hiltViewModel(),
    onNavigateSettingsScreen: () -> Unit,

    ) {
    val snackbarHostState =
        remember { SnackbarHostState() }

    val context = LocalContext.current
    LaunchedEffect(key1 = true) {
        viewModel.eventFlow.collectLatest { message ->
            snackbarHostState.showSnackbar(message)

        }
    }

    CategoryScreenContent(
        state = viewModel.state,
        snackbarHostState = snackbarHostState,
        onEvent = viewModel::onEvent,
        onNavigateSettingsScreen = onNavigateSettingsScreen,

        )

}

@Composable
fun CategoryScreenContent(
    state: CategoryState,
    snackbarHostState: SnackbarHostState,
    onEvent: (CategoryEvent) -> Unit,
    onNavigateSettingsScreen: () -> Unit,
) {
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = { CategoryTopBar(onNavigateSettingsScreen) },
        bottomBar = {
            CategoryInput(
                color = Color(state.categoryColor),
                onChangeColor = { onEvent(CategoryEvent.OnCategoryColorChange(it)) },
                categoryName = state.categoryName ?: "",
                onNameChange = { onEvent(CategoryEvent.OnCategoryNameChange(it)) },
                onAddCategory = { onEvent(CategoryEvent.OnAddCategory) }
            )
            // Spacer(modifier = Modifier.padding(bottom = 70.dp))
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
        ) {
            TabRow(selectedTabIndex = if (state.isExpenseCategory) 0 else 1) {
                Tab(
                    selected = state.isExpenseCategory,
                    onClick = { onEvent(CategoryEvent.OnCategoryTypeChange(isExpenseCategory = true)) },
                    text = { Text(text = "Expense Categories") })

                Tab(
                    selected = !state.isExpenseCategory,
                    onClick = { onEvent(CategoryEvent.OnCategoryTypeChange(isExpenseCategory = false)) },
                    text = { Text(text = "Income Categories") })
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                LazyColumn(modifier = Modifier.wrapContentSize()) {
                    if (state.isExpenseCategory) {
                        items(items = state.expenseCategories, key = { it.id }) { category ->
                            CategoryItem(
                                category = category,
                                onDelete = { onEvent(CategoryEvent.OnCategoryDelete(category)) })
                        }
                    } else {
                        items(items = state.incomeCategories, key = { it.id }) { category ->
                            CategoryItem(
                                category = category,
                                onDelete = { onEvent(CategoryEvent.OnCategoryDelete(category)) })
                        }
                    }

                }
            }
        }
    }
}

@Composable
fun ColorListItem(modifier: Modifier = Modifier, color: Color, onClick: () -> Unit) {
    Box(
        modifier = modifier
            .size(30.dp)
            .clip(CircleShape)
            .background(color)
            .border(
                width =2.dp,
                color = MaterialTheme.colorScheme.onSurface,
                shape = CircleShape
            )
            .clickable { onClick() }
    )
}

@Composable
fun CategoryInput(
    modifier: Modifier = Modifier,
    color: Color,
    onChangeColor: (Int) -> Unit,
    categoryName: String,
    onNameChange: (String) -> Unit,
    onAddCategory: () -> Unit
) {
    var isVisible by remember { mutableStateOf(false) }
    Column {
        AnimatedVisibility(visible = isVisible) {
            LazyRow {
                items(Category.colors) { colorCode ->
                    ColorListItem(
                        modifier=Modifier.padding(8.dp),
                        color = Color(colorCode),
                        onClick = {
                            onChangeColor(colorCode)
                            isVisible=false
                        })
                }
            }
        }
        Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
        ColorListItem(color = color, onClick = { isVisible = !isVisible })
        TextField(
            value = categoryName, onValueChange = { onNameChange(it) },
            modifier = Modifier.weight(1f),
            singleLine = true,
            //trailingIcon = {Icon(Icons.Default.Close,"")},
            placeholder = { Text(text = "New category") }
        )
        FilledIconButton(
            modifier = Modifier,
            onClick = { onAddCategory() },
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "",
            )
        }

    }
    }




}

@Composable
fun CategoryItem(
    modifier: Modifier = Modifier,
    category: com.novacodestudios.mint.model.Category,
    onDelete: () -> Unit
) {
    SwipeToDeleteContainer(item = category, onDelete = { onDelete() }) {
        Column {
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp, horizontal = 16.dp),
                text = category.name
            )
            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
        }

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryTopBar(onNavigateSettingsScreen: () -> Unit) {
    TopAppBar(
        title = { Text(text = "Categories") },
        navigationIcon = {
            IconButton(
                onClick = { onNavigateSettingsScreen() },
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = ""
                )
            }
        })
}

@Preview(showBackground = true)
@Composable
private fun CategoryScreenPreview() {
    MintTheme {
        CategoryScreenContent(
            state = CategoryState(
                expenseCategories = listOf(
                    ExpenseCategory(
                        id = 3,
                        name = "Market",
                        color = 0
                    ),
                    ExpenseCategory(id = 0, name = "Market", color = 0),
                    ExpenseCategory(id = 1, name = "Market", color = 0),
                    ExpenseCategory(id = 2, name = "Market", color = 0)
                )
            ),
            snackbarHostState = SnackbarHostState(),
            onEvent = {},
            onNavigateSettingsScreen = {}

        )
    }

}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun <T> SwipeToDeleteContainer(
    item: T,
    onDelete: (T) -> Unit,
    animationDuration: Int = 500,
    content: @Composable (T) -> Unit
) {
    var isRemoved by remember {
        mutableStateOf(false)
    }
    val state = rememberDismissState(
        confirmStateChange = { value ->
            if (value == DismissValue.DismissedToStart) {
                isRemoved = true
                true
            } else {
                false
            }
        }
    )

    LaunchedEffect(key1 = isRemoved) {
        if (isRemoved) {
            delay(animationDuration.toLong())
            onDelete(item)
        }
    }

    AnimatedVisibility(
        visible = !isRemoved,
        exit = shrinkVertically(
            animationSpec = tween(durationMillis = animationDuration),
            shrinkTowards = Alignment.Top
        ) + fadeOut()
    ) {
        SwipeToDismiss(
            state = state,
            background = {
                DeleteBackground(swipeDismissState = state)
            },
            dismissContent = { content(item) },
            directions = setOf(DismissDirection.EndToStart)
        )
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun DeleteBackground(
    swipeDismissState: DismissState
) {
    val color = if (swipeDismissState.dismissDirection == DismissDirection.EndToStart) {
        Color.Red
    } else Color.Transparent

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color)
            .padding(16.dp),
        contentAlignment = Alignment.CenterEnd
    ) {
        Icon(
            imageVector = Icons.Default.Delete,
            contentDescription = null,
            tint = if (color == Color.Red) Color.White else Color.Transparent
        )
    }
}