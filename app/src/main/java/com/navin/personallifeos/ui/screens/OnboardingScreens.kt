package com.navin.personallifeos.ui.screens

import android.Manifest
import android.app.AlarmManager
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Alarm
import androidx.compose.material.icons.outlined.Mic
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat

@Composable
fun OnboardingFlow(onFinish: () -> Unit) {
    var step by remember { mutableIntStateOf(0) }
    when (step) {
        0 -> WelcomeOnboardingScreen(onContinue = { step = 1 }, onSkip = onFinish)
        else -> PermissionSetupScreen(onFinish = onFinish)
    }
}

@Composable
private fun WelcomeOnboardingScreen(
    onContinue: () -> Unit,
    onSkip: () -> Unit,
) {
    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Column(
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 34.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp),
        ) {
            Text("WELCOME", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
            Text("A calmer way to organize your life and growth", style = MaterialTheme.typography.displaySmall)
            Text(
                "Plans, reminders, diary moments, hobbies, learning, goals and the projects you’re building — connected in one private system.",
                style = MaterialTheme.typography.bodyLarge,
            )
            WarmCard(
                "One capture box",
                "Write or speak naturally. Personal Life OS sorts the thought after you capture it, not before.",
            )
            WarmCard(
                "Your life stays connected",
                "A completed task can update a project, appear in Journey and become part of your weekly review.",
            )
            WarmCard(
                "Support, not judgement",
                "No guilt-heavy streaks. The app uses gentle signals to help you notice what is moving and what needs care.",
            )
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.Bottom) {
                Button(
                    onClick = onContinue,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                ) { Text("Continue") }
                OutlinedButton(
                    onClick = onSkip,
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                    shape = RoundedCornerShape(18.dp),
                ) { Text("Skip setup") }
            }
        }
    }
}

@Composable
private fun PermissionSetupScreen(onFinish: () -> Unit) {
    val context = LocalContext.current
    val notificationLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { }
    val micLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { }

    val notificationsGranted = Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
        ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
    val micGranted = ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED
    val alarmManager = context.getSystemService(AlarmManager::class.java)
    val exactGranted = Build.VERSION.SDK_INT < Build.VERSION_CODES.S || alarmManager.canScheduleExactAlarms()

    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Column(
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 34.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Text("PERMISSIONS & RELIABILITY", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
            Text("Let the important parts work well", style = MaterialTheme.typography.displaySmall)
            Text(
                "You can continue without optional permissions. We’ll always show clearly when a feature is limited.",
                style = MaterialTheme.typography.bodyMedium,
            )

            PermissionCard(
                icon = { Icon(Icons.Outlined.Notifications, contentDescription = null) },
                title = "Notifications",
                body = "Needed for reminders and follow-ups.",
                state = if (notificationsGranted) "Allowed" else "Allow",
                onClick = {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        notificationLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                    }
                },
            )
            PermissionCard(
                icon = { Icon(Icons.Outlined.Alarm, contentDescription = null) },
                title = "Exact alarms",
                body = "Recommended for reminders that must fire at the exact time.",
                state = if (exactGranted) "Ready" else "Open settings",
                onClick = {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        context.startActivity(
                            Intent(
                                Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM,
                                Uri.parse("package:${context.packageName}"),
                            ),
                        )
                    }
                },
            )
            PermissionCard(
                icon = { Icon(Icons.Outlined.Mic, contentDescription = null) },
                title = "Microphone",
                body = "Optional. Enables voice capture in a later interaction pass.",
                state = if (micGranted) "Allowed" else "Allow",
                onClick = { micLauncher.launch(Manifest.permission.RECORD_AUDIO) },
            )

            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.Bottom) {
                Button(
                    onClick = onFinish,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                ) { Text("Finish setup") }
            }
        }
    }
}

@Composable
private fun PermissionCard(
    icon: @Composable () -> Unit,
    title: String,
    body: String,
    state: String,
    onClick: () -> Unit,
) {
    Surface(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surface,
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            icon()
            Column(modifier = Modifier.weight(1f)) {
                Text(title, style = MaterialTheme.typography.titleMedium)
                Text(body, style = MaterialTheme.typography.bodySmall)
            }
            Text(state, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
        }
    }
}
