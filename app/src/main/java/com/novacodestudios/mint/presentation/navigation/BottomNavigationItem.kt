package com.novacodestudios.mint.presentation.navigation

import androidx.compose.ui.graphics.vector.ImageVector

data class BottomNavigationItem(
    val  title:String,
    val selectedIcon: ImageVector,
    val unSelectedIcon:ImageVector,
    val route:String
)