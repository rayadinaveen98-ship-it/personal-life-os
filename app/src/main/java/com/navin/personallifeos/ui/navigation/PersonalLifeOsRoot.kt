package com.navin.personallifeos.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.navin.personallifeos.ui.screens.CaptureScreen
import com.navin.personallifeos.ui.screens.JourneyScreen
import com.navin.personallifeos.ui.screens.MeScreen
import com.navin.personallifeos.ui.screens.OnboardingFlow
import com.navin.personallifeos.ui.screens.PlanScreen
import com.navin.personallifeos.ui.screens.TodayScreen
import com.navin.personallifeos.ui.viewmodel.AppEntryViewModel

private const val CaptureRoute = "capture"

@Composable
fun PersonalLifeOsRoot(entryViewModel: AppEntryViewModel = hiltViewModel()) {
    val onboardingComplete by entryViewModel.onboardingComplete.collectAsState()
    if (!onboardingComplete) {
        OnboardingFlow(onFinish = entryViewModel::finishOnboarding)
        return
    }

    val navController = rememberNavController()
    val destinations = listOf(
        AppDestination.Today,
        AppDestination.Plan,
        AppDestination.Journey,
        AppDestination.Me,
    )
    val backStack by navController.currentBackStackEntryAsState()
    val currentRoute = backStack?.destination?.route
    val showChrome = currentRoute != CaptureRoute

    fun navigateTo(destination: AppDestination) {
        navController.navigate(destination.route) {
            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
            launchSingleTop = true
            restoreState = true
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            if (showChrome) {
                NavigationBar(
                    modifier = Modifier.height(82.dp),
                    containerColor = MaterialTheme.colorScheme.surface,
                    tonalElevation = 0.dp,
                ) {
                    destinations.take(2).forEach { destination ->
                        NavigationBarItem(
                            selected = currentRoute == destination.route,
                            onClick = { navigateTo(destination) },
                            icon = { Icon(destination.icon, contentDescription = destination.label) },
                            label = { Text(destination.label) },
                            colors = observatoryNavColors(),
                        )
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight(),
                        contentAlignment = Alignment.Center,
                    ) {
                        Surface(
                            onClick = { navController.navigate(CaptureRoute) },
                            modifier = Modifier.size(60.dp),
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primary,
                            shadowElevation = 8.dp,
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    Icons.Rounded.Add,
                                    contentDescription = "Capture",
                                    tint = MaterialTheme.colorScheme.onPrimary,
                                    modifier = Modifier.size(30.dp),
                                )
                            }
                        }
                    }

                    destinations.drop(2).forEach { destination ->
                        NavigationBarItem(
                            selected = currentRoute == destination.route,
                            onClick = { navigateTo(destination) },
                            icon = { Icon(destination.icon, contentDescription = destination.label) },
                            label = { Text(destination.label) },
                            colors = observatoryNavColors(),
                        )
                    }
                }
            }
        },
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = AppDestination.Today.route,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            composable(AppDestination.Today.route) { TodayScreen() }
            composable(AppDestination.Plan.route) { PlanScreen() }
            composable(AppDestination.Journey.route) { JourneyScreen() }
            composable(AppDestination.Me.route) { MeScreen() }
            composable(CaptureRoute) { CaptureScreen(onClose = { navController.popBackStack() }) }
        }
    }
}

@Composable
private fun observatoryNavColors() = NavigationBarItemDefaults.colors(
    selectedIconColor = MaterialTheme.colorScheme.primary,
    selectedTextColor = MaterialTheme.colorScheme.onSurface,
    indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
    unselectedIconColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.48f),
    unselectedTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.58f),
)
