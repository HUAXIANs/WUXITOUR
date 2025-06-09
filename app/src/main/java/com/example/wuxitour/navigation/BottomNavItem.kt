package com.example.wuxitour.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * 底部导航项
 */
sealed class BottomNavItem(
    val route: String,
    val title: String,
    val icon: ImageVector
) {
    object Home : BottomNavItem("home", "首页", Icons.Default.Home)
    object Attractions : BottomNavItem("attractions", "景点", Icons.Default.Place)
    object Trip : BottomNavItem("trip", "行程", Icons.Default.Map)
    object Profile : BottomNavItem("profile", "我的", Icons.Default.Person)
}

/**
 * 所有导航项列表
 */
val bottomNavItems = listOf(
    BottomNavItem.Home,
    BottomNavItem.Attractions,
    BottomNavItem.Trip,
    BottomNavItem.Profile
)

