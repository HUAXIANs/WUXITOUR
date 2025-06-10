package com.example.wuxitour

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.wuxitour.navigation.BottomNavItem
import com.example.wuxitour.ui.screens.attractions.AttractionsScreen
import com.example.wuxitour.ui.screens.detail.AttractionDetailScreen
import com.example.wuxitour.ui.screens.home.HomeScreen
import com.example.wuxitour.ui.screens.profile.ProfileScreen
import com.example.wuxitour.ui.screens.trip.TripScreen
import com.example.wuxitour.ui.theme.WuxiTourTheme
import com.example.wuxitour.ui.screens.trip_detail.TripDetailScreen

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WuxiTourTheme {
                val navController = rememberNavController()
                Scaffold(
                    bottomBar = { BottomNavigationBar(navController) }
                ) { innerPadding ->
                    AppNavigation(navController, Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun AppNavigation(navController: NavHostController, modifier: Modifier = Modifier) {
    NavHost(
        navController = navController,
        startDestination = BottomNavItem.Home.route,
        modifier = modifier
    ) {
        composable(BottomNavItem.Home.route) {
            HomeScreen(onAttractionClick = { id -> navController.navigate("attraction_detail/$id") })
        }
        composable(BottomNavItem.Attractions.route) {
            AttractionsScreen(onAttractionClick = { id -> navController.navigate("attraction_detail/$id") })
        }
        composable(BottomNavItem.Trip.route) {
            TripScreen(onNavigateToTripDetail = { tripId ->
                navController.navigate("trip_detail/$tripId")
            }
            )
        }
        composable(BottomNavItem.Profile.route) {
            ProfileScreen(onNavigateToAttraction = { id -> navController.navigate("attraction_detail/$id") })
        }
        composable(
            route = "attraction_detail/{attractionId}",
            arguments = listOf(navArgument("attractionId") { type = NavType.StringType })
        ) { backStackEntry ->
            val attractionId = backStackEntry.arguments?.getString("attractionId")
            requireNotNull(attractionId)
            AttractionDetailScreen(
                attractionId = attractionId,
                onBackClick = { navController.popBackStack() }
            )
        }
        composable(
            route = "trip_detail/{tripId}",
            arguments = listOf(navArgument("tripId") { type = NavType.StringType })
        ) { backStackEntry ->
            val tripId = backStackEntry.arguments?.getString("tripId")
            requireNotNull(tripId)
            // TripDetailScreen 是我们下一阶段要完善的页面
            TripDetailScreen(
                tripId = tripId,
                onBackClick = { navController.popBackStack() },
                onNavigateToAttraction = { attractionId ->
                    navController.navigate("attraction_detail/$attractionId")
                }
            )
        }
    }
}

@Composable
fun BottomNavigationBar(navController: NavHostController) {
    val items = listOf(
        BottomNavItem.Home,
        BottomNavItem.Attractions,
        BottomNavItem.Trip,
        BottomNavItem.Profile
    )
    NavigationBar {
        val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
        items.forEach { item ->
            NavigationBarItem(
                icon = { Icon(item.icon, contentDescription = item.title) },
                label = { Text(item.title) },
                selected = currentRoute == item.route,
                onClick = {
                    navController.navigate(item.route) {
                        popUpTo(navController.graph.startDestinationId) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}