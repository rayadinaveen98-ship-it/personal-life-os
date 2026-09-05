package com.navin.personallifeos.ui.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
import com.navin.personallifeos.ui.theme.CardCream
import com.navin.personallifeos.ui.theme.Ink
import com.navin.personallifeos.ui.theme.Moss
import com.navin.personallifeos.ui.theme.MossSoft
import com.navin.personallifeos.ui.viewmodel.AppEntryViewModel

private const val CaptureRoute = "capture"

@Composable
fun PersonalLifeOsRoot(entryViewModel: AppEntryViewModel = hiltViewModel()) {
    val onboardingComplete by entryViewModel.onboardingComplete.collectAsState()
    val preferredName by entryViewModel.preferredName.collectAsState()

    if (!onboardingComplete) {
        OnboardingFlow(onFinish = entryViewModel::finishOnboarding)
        return
    }

    val navController = rememberNavController()
    val backStack by navController.currentBackStackEntryAsState()
    val currentRoute = backStack?.destination?.route ?: AppDestination.Today.route

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
            ExactMockBottomBar(
                currentRoute = currentRoute,
                onToday = { navigateTo(AppDestination.Today) },
                onPlan = { navigateTo(AppDestination.Plan) },
                onCapture = {
                    if (currentRoute != CaptureRoute) navController.navigate(CaptureRoute)
                },
                onJourney = { navigateTo(AppDestination.Journey) },
                onMe = { navigateTo(AppDestination.Me) },
            )
        },
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = AppDestination.Today.route,
            modifier = Modifier.fillMaxSize().padding(innerPadding),
        ) {
            composable(AppDestination.Today.route) { TodayScreen(preferredName = preferredName) }
            composable(AppDestination.Plan.route) { PlanScreen() }
            composable(AppDestination.Journey.route) { JourneyScreen() }
            composable(AppDestination.Me.route) { MeScreen() }
            composable(CaptureRoute) { CaptureScreen(onClose = { navController.popBackStack() }) }
        }
    }
}

@Composable
private fun ExactMockBottomBar(
    currentRoute: String,
    onToday: () -> Unit,
    onPlan: () -> Unit,
    onCapture: () -> Unit,
    onJourney: () -> Unit,
    onMe: () -> Unit,
) {
    val todayStyle = currentRoute == AppDestination.Today.route
    val barHeight = if (todayStyle) 86.dp else if (currentRoute == AppDestination.Me.route) 82.dp else 76.dp
    val barShape = if (todayStyle) RoundedCornerShape(0.dp) else RoundedCornerShape(26.dp)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = if (todayStyle) 0.dp else 14.dp, vertical = if (todayStyle) 0.dp else 8.dp),
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth().height(barHeight),
            shape = barShape,
            color = CardCream.copy(alpha = 0.98f),
            shadowElevation = if (todayStyle) 0.dp else 6.dp,
        ) {
            Row(
                modifier = Modifier.fillMaxSize().padding(horizontal = if (todayStyle) 16.dp else 8.dp, vertical = 7.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                ExactNavItem(AppDestination.Today.icon, "Today", currentRoute == AppDestination.Today.route, onToday, todayStyle)
                ExactNavItem(AppDestination.Plan.icon, "Plan", currentRoute == AppDestination.Plan.route, onPlan, todayStyle)
                CaptureNavButton(
                    active = currentRoute == CaptureRoute,
                    todayStyle = todayStyle,
                    onClick = onCapture,
                )
                ExactNavItem(AppDestination.Journey.icon, "Journey", currentRoute == AppDestination.Journey.route, onJourney, todayStyle)
                ExactNavItem(AppDestination.Me.icon, "Me", currentRoute == AppDestination.Me.route, onMe, todayStyle)
            }
        }
    }
}

@Composable
private fun ExactNavItem(
    icon: ImageVector,
    label: String,
    active: Boolean,
    onClick: () -> Unit,
    todayStyle: Boolean,
) {
    Surface(onClick = onClick, color = Color.Transparent, modifier = Modifier.size(width = 58.dp, height = 60.dp)) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
            Box(
                modifier = Modifier
                    .size(if (todayStyle) 26.dp else 28.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (active) MossSoft else Color.Transparent),
                contentAlignment = Alignment.Center,
            ) {
                Icon(icon, contentDescription = label, tint = if (active) Moss else Color(0xFF8A857A), modifier = Modifier.size(20.dp))
            }
            Text(
                label,
                fontSize = if (todayStyle) 10.sp else 9.5.sp,
                fontWeight = if (active) FontWeight.ExtraBold else FontWeight.Bold,
                color = if (active) Moss else Color(0xFF8A857A),
                modifier = Modifier.padding(top = 4.dp),
            )
        }
    }
}

@Composable
private fun CaptureNavButton(active: Boolean, todayStyle: Boolean, onClick: () -> Unit) {
    val bg = if (todayStyle) Ink else Moss
    val rounded = if (todayStyle) RoundedCornerShape(18.dp) else if (active) CircleShape else RoundedCornerShape(20.dp)
    Surface(onClick = onClick, color = Color.Transparent, modifier = Modifier.size(width = 62.dp, height = 72.dp)) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
            Surface(
                onClick = onClick,
                modifier = Modifier.size(if (todayStyle) 58.dp else 54.dp).offset(y = (-8).dp),
                shape = rounded,
                color = bg,
                shadowElevation = 7.dp,
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(Icons.Rounded.Add, contentDescription = "Capture", tint = Color.White, modifier = Modifier.size(29.dp))
                }
            }
            if (todayStyle || active) {
                Text("Capture", fontSize = 9.5.sp, fontWeight = FontWeight.ExtraBold, color = if (todayStyle) Ink else Moss, modifier = Modifier.offset(y = (-6).dp))
            }
        }
    }
}
