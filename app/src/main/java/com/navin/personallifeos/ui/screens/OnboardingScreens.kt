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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Alarm
import androidx.compose.material.icons.outlined.Mic
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.navin.personallifeos.ui.theme.CardCream
import com.navin.personallifeos.ui.theme.InkMuted
import com.navin.personallifeos.ui.theme.LavenderSoft
import com.navin.personallifeos.ui.theme.MossSoft

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
    val selected = remember { mutableStateListOf("Projects", "Diary", "Learning") }
    val focusAreas = listOf("Projects", "Diary", "Reminders", "Habits", "Learning", "Hobbies", "Goals")
    val lifeAreas = listOf("Game Development", "App Building", "Filmmaking", "Health", "Reading")

    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 18.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Eyebrow("Welcome to Personal Life OS")
            PageTitle("A calmer way to organize your life and growth")
            Text(
                "Track plans, reminders, diary moments, hobbies, learning, goals and the projects you’re building — all in one thoughtful system.",
                style = MaterialTheme.typography.bodyLarge,
                color = InkMuted,
            )

            Surface(
                shape = RoundedCornerShape(24.dp),
                color = CardCream,
                shadowElevation = 2.dp,
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    Text("What this app helps you do", style = MaterialTheme.typography.titleMedium)
                    Text(
                        "Capture anything quickly, see what matters today, remember meaningful moments, and gradually understand how your life is actually moving.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = InkMuted,
                    )
                    MetaRow("Today", "Plan", "Capture", "Journey", "Me")
                }
            }

            Surface(
                shape = RoundedCornerShape(20.dp),
                color = MossSoft,
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    Text("What matters most right now?", style = MaterialTheme.typography.titleMedium)
                    ChipWrap(
                        labels = focusAreas,
                        selected = selected.toSet(),
                        onToggle = { label ->
                            if (selected.contains(label)) selected.remove(label) else selected.add(label)
                        },
                    )
                }
            }

            Surface(
                shape = RoundedCornerShape(20.dp),
                color = LavenderSoft,
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    Text("A few current life areas", style = MaterialTheme.typography.titleMedium)
                    ChipWrap(labels = lifeAreas, selected = emptySet(), onToggle = {})
                    Text(
                        "You can shape these properly later. This is only to make your first week feel personal.",
                        style = MaterialTheme.typography.bodySmall,
                        color = InkMuted,
                    )
                }
            }

            Button(
                onClick = onContinue,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
            ) { Text("Continue") }

            TextButton(onClick = onSkip, modifier = Modifier.align(Alignment.CenterHorizontally)) {
                Text("Maybe later")
            }

            Text(
                "Private by default · Built to support your life, not judge it",
                style = MaterialTheme.typography.labelSmall,
                color = InkMuted,
                modifier = Modifier.align(Alignment.CenterHorizontally),
            )
        }
    }
}

@Composable
private fun ChipWrap(
    labels: List<String>,
    selected: Set<String>,
    onToggle: (String) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        labels.chunked(3).forEach { rowLabels ->
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                rowLabels.forEach { label ->
                    FilterChip(
                        selected = label in selected,
                        onClick = { onToggle(label) },
                        label = { Text(label) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.16f),
                            selectedLabelColor = MaterialTheme.colorScheme.onSurface,
                        ),
                    )
                }
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
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Eyebrow("Permissions & reliability")
            PageTitle("Let the important parts work well")
            Text(
                "To make reminders reliable and capture effortless, the app needs a few permissions. You can continue without optional ones.",
                style = MaterialTheme.typography.bodyLarge,
                color = InkMuted,
            )

            PermissionCard(
                icon = { Icon(Icons.Outlined.Notifications, contentDescription = null) },
                title = "Notifications",
                body = "Needed for reminders, follow-ups and review prompts.",
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
                body = "Recommended when a reminder must fire at the exact time.",
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
                body = "Optional. Lets Universal Capture understand spoken thoughts.",
                state = if (micGranted) "Allowed" else "Allow",
                onClick = { micLauncher.launch(Manifest.permission.RECORD_AUDIO) },
            )

            AccentCard(
                eyebrow = "Gentle guidance",
                title = "We’ll tell you when something is limited",
                body = "If Android blocks an alarm or background action later, the app should explain why instead of silently failing.",
                containerColor = LavenderSoft,
            )

            Button(
                onClick = onFinish,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
            ) { Text("Finish setup") }

            OutlinedButton(
                onClick = onFinish,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
            ) { Text("Continue with limited features") }
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
        color = CardCream,
        shadowElevation = 1.dp,
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Surface(shape = RoundedCornerShape(15.dp), color = MossSoft) {
                androidx.compose.foundation.layout.Box(
                    modifier = Modifier.padding(11.dp),
                    contentAlignment = Alignment.Center,
                ) { icon() }
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(title, style = MaterialTheme.typography.titleMedium)
                Text(body, style = MaterialTheme.typography.bodySmall, color = InkMuted)
            }
            Text(state, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
        }
    }
}
