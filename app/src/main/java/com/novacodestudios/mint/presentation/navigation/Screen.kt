package com.novacodestudios.mint.presentation.navigation

sealed class Screen(val route:String) {
    data object AdditionScreen:Screen("addition_screen")
    data object CategoryScreen:Screen("category_screen")
    data object ExpenseScreen:Screen("expense_screen")
    data object ReportScreen:Screen("report_screen")
    data object SettingsScreen:Screen("settings_screen")
    data object EditScreen:Screen("edit_screen")
}