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
import com.example.wuxitour.ui.screens.guide.GuideScreen
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.wuxitour.data.api.NetworkConfig
import com.example.wuxitour.data.repository.AttractionRepository
import com.example.wuxitour.data.repository.GuideRepository
import com.example.wuxitour.data.repository.FirebaseTripRepository
import com.example.wuxitour.data.repository.FirebaseUserRepository
import com.example.wuxitour.ui.screens.attractions.AttractionsViewModel
import com.example.wuxitour.ui.screens.detail.AttractionDetailViewModel
import com.example.wuxitour.ui.screens.guide.GuideViewModel
import com.example.wuxitour.ui.screens.home.HomeViewModel
import com.example.wuxitour.ui.screens.profile.ProfileViewModel
import com.example.wuxitour.ui.screens.trip.TripViewModel
import com.example.wuxitour.ui.screens.trip_detail.TripDetailViewModel
import androidx.lifecycle.viewmodel.MutableCreationExtras
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.createSavedStateHandle
import dagger.hilt.android.AndroidEntryPoint
import androidx.hilt.navigation.compose.hiltViewModel

@AndroidEntryPoint
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
            val homeViewModel: HomeViewModel = hiltViewModel()
            HomeScreen(
                viewModel = homeViewModel,
                onAttractionClick = { id -> navController.navigate("attraction_detail/$id") }
            )
        }
        composable(BottomNavItem.Attractions.route) {
            val attractionsViewModel: AttractionsViewModel = hiltViewModel()
            AttractionsScreen(
                viewModel = attractionsViewModel,
                onAttractionClick = { id -> navController.navigate("attraction_detail/$id") }
            )
        }
        composable(BottomNavItem.Trip.route) {
            val tripViewModel: TripViewModel = hiltViewModel()
            TripScreen(
                viewModel = tripViewModel,
                onNavigateToTripDetail = { tripId ->
                    navController.navigate("trip_detail/$tripId")
                }
            )
        }
        composable(BottomNavItem.Profile.route) {
            val profileViewModel: ProfileViewModel = hiltViewModel()
            ProfileScreen(
                viewModel = profileViewModel,
                onNavigateToAttraction = { id -> navController.navigate("attraction_detail/$id") }
            )
        }
        composable(BottomNavItem.Guide.route) {
            val guideViewModel: GuideViewModel = hiltViewModel()
            GuideScreen(
                viewModel = guideViewModel
            )
        }
        composable(
            route = "attraction_detail/{attractionId}",
            arguments = listOf(navArgument("attractionId") { type = NavType.StringType })
        ) { backStackEntry ->
            val attractionId = backStackEntry.arguments?.getString("attractionId")
            requireNotNull(attractionId)
            val attractionDetailViewModel: AttractionDetailViewModel = hiltViewModel()
            AttractionDetailScreen(
                viewModel = attractionDetailViewModel,
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
            val tripDetailViewModel: TripDetailViewModel = hiltViewModel()
            TripDetailScreen(
                viewModel = tripDetailViewModel,
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
        BottomNavItem.Profile,
        BottomNavItem.Guide
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