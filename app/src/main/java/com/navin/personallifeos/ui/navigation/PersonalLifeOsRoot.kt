package com.navin.personallifeos.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.weight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
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

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            if (showChrome) {
                NavigationBar(containerColor = MaterialTheme.colorScheme.surface) {
                    destinations.take(2).forEach { destination ->
                        NavigationBarItem(
                            selected = currentRoute == destination.route,
                            onClick = {
                                navController.navigate(destination.route) {
                                    popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = { Icon(destination.icon, contentDescription = destination.label) },
                            label = { Text(destination.label) },
                        )
                    }

                    Box(modifier = Modifier.weight(1f))

                    destinations.drop(2).forEach { destination ->
                        NavigationBarItem(
                            selected = currentRoute == destination.route,
                            onClick = {
                                navController.navigate(destination.route) {
                                    popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = { Icon(destination.icon, contentDescription = destination.label) },
                            label = { Text(destination.label) },
                        )
                    }
                }
            }
        },
        floatingActionButton = {
            if (showChrome) {
                FloatingActionButton(
                    onClick = { navController.navigate(CaptureRoute) },
                    containerColor = MaterialTheme.colorScheme.primary,
                ) {
                    Icon(Icons.Rounded.Add, contentDescription = "Capture")
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
