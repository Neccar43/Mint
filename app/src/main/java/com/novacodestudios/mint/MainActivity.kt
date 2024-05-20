package com.novacodestudios.mint

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.AddCircleOutline
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Paid
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.AddCircle
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.Paid
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.novacodestudios.mint.presentation.addition.AdditionScreen
import com.novacodestudios.mint.presentation.category.CategoryScreen
import com.novacodestudios.mint.presentation.edit.EditScreen
import com.novacodestudios.mint.presentation.expense.ExpenseScreen
import com.novacodestudios.mint.presentation.navigation.BottomNavigationItem
import com.novacodestudios.mint.presentation.navigation.Graph
import com.novacodestudios.mint.presentation.navigation.Screen
import com.novacodestudios.mint.presentation.report.ReportScreen
import com.novacodestudios.mint.presentation.settings.SettingsScreen
import com.novacodestudios.mint.ui.theme.MintTheme
import com.novacodestudios.mint.util.Const
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()
            MintTheme {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.parent?.route
                Scaffold(modifier = Modifier.fillMaxSize(), bottomBar = {
                    if (currentRoute == Graph.MAIN) {
                        MintBottomBar(navController)
                    }

                }) { innerPadding ->
                    NavHost(
                        modifier = Modifier.padding(innerPadding),
                        navController = navController,
                        startDestination = Graph.MAIN,
                        route = Graph.ROOT
                    ) {
                        mainGraph(navController)
                        composable(route = Screen.CategoryScreen.route) {
                            CategoryScreen(onNavigateSettingsScreen = {
                                navController.navigate(Screen.SettingsScreen.route){
                                    popUpTo(Graph.MAIN){
                                        inclusive=true
                                    }
                                }
                            })
                        }

                        composable(
                            route = Screen.EditScreen.route + "/{${Const.IS_EXPENSE}}/{${Const.TRANSACTION_ID}}",
                            arguments = listOf(
                                navArgument(Const.IS_EXPENSE) {
                                    type = NavType.BoolType
                                },
                                navArgument(Const.TRANSACTION_ID){
                                    type=NavType.IntType
                                }
                            )
                        ){
                            EditScreen(onNavigateExpenseScreen = {
                                navController.navigate(Screen.ExpenseScreen.route){
                                    popUpTo(Graph.MAIN){
                                        inclusive=true
                                    }
                                }
                            })
                        }
                    }
                }
            }
        }
    }

    fun NavGraphBuilder.mainGraph(navController: NavController) {
        navigation(route = Graph.MAIN, startDestination = Screen.ExpenseScreen.route) {
            composable(route = Screen.ExpenseScreen.route) {
                ExpenseScreen(onNavigateEditScreen ={isExpense,id->
                    navController.navigate(Screen.EditScreen.route+"/${isExpense}/${id}")
                })
            }
            composable(route = Screen.ReportScreen.route) {
                ReportScreen()
            }
            composable(route = Screen.SettingsScreen.route) {
                SettingsScreen(onNavigateCategoryScreen = {
                    navController.navigate(route = Screen.CategoryScreen.route)
                })
            }
            composable(route = Screen.AdditionScreen.route) {
                AdditionScreen(
                    onNavigateExpenseScreen = {
                        navController.navigate(Screen.ExpenseScreen.route) {

                        }
                    }
                )
            }
        }
    }

    @Composable
    fun MintBottomBar(navController: NavController) {
        var selectedItemIndex by rememberSaveable { mutableIntStateOf(0) }
        val items = listOf(
            BottomNavigationItem(
                title = "Expenses",
                selectedIcon = Icons.Filled.Paid,
                unSelectedIcon = Icons.Outlined.Paid,
                route = Screen.ExpenseScreen.route,
            ),
            BottomNavigationItem(
                title = "Reports",
                selectedIcon = Icons.Filled.BarChart,
                unSelectedIcon = Icons.Outlined.BarChart,
                route = Screen.ReportScreen.route,
            ),
            BottomNavigationItem(
                title = "Addition",
                selectedIcon = Icons.Filled.AddCircle,
                unSelectedIcon = Icons.Outlined.AddCircle,
                route = Screen.AdditionScreen.route,
            ),
            BottomNavigationItem(
                title = "Settings",
                selectedIcon = Icons.Filled.Settings,
                unSelectedIcon = Icons.Outlined.Settings,
                route = Screen.SettingsScreen.route,
            ),
        )

        NavigationBar {
            items.forEachIndexed { index, item ->
                NavigationBarItem(
                    label = { Text(text = item.title) },
                    alwaysShowLabel = false,
                    selected = selectedItemIndex == index,
                    onClick = {
                        selectedItemIndex = index
                        navController.navigate(item.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    icon = {
                        Icon(
                            imageVector = if (selectedItemIndex == index) {
                                item.selectedIcon
                            } else {
                                item.unSelectedIcon
                            },
                            contentDescription = item.title
                        )
                    })
            }
        }
    }
}