package com.navin.personallifeos.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.AutoStories
import androidx.compose.material.icons.outlined.CheckCircleOutline
import androidx.compose.material.icons.outlined.Home
import androidx.compose.ui.graphics.vector.ImageVector

sealed class AppDestination(
    val route: String,
    val label: String,
    val icon: ImageVector,
) {
    data object Today : AppDestination("today", "Today", Icons.Outlined.Home)
    data object Plan : AppDestination("plan", "Plan", Icons.Outlined.CheckCircleOutline)
    data object Journey : AppDestination("journey", "Journey", Icons.Outlined.AutoStories)
    data object Me : AppDestination("me", "Me", Icons.Outlined.AccountCircle)
}
