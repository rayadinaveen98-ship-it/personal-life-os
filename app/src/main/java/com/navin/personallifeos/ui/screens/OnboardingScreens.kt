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
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Alarm
import androidx.compose.material.icons.outlined.Mic
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.navin.personallifeos.ui.theme.CardCream
import com.navin.personallifeos.ui.theme.InkMuted
import com.navin.personallifeos.ui.theme.LavenderSoft
import com.navin.personallifeos.ui.theme.Moss
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
private fun WelcomeOnboardingScreen(onContinue: () -> Unit, onSkip: () -> Unit) {
    val chosen = remember { mutableStateOf(emptySet<String>()) }

    Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        Box(modifier = Modifier.size(240.dp).offset(x = 250.dp, y = (-55).dp).background(Color(0xFFD5E1CF).copy(alpha = 0.20f), CircleShape))
        Box(modifier = Modifier.size(180.dp).offset(x = (-55).dp, y = 660.dp).background(Color(0xFFE6D9C1).copy(alpha = 0.20f), CircleShape))

        Column(
            modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 20.dp, vertical = 24.dp),
        ) {
            Text("WELCOME TO PERSONAL LIFE OS", fontSize = 11.sp, letterSpacing = 1.3.sp, color = Moss, fontWeight = FontWeight.Bold)
            Text(
                "A calmer way to\norganize your life\nand growth",
                fontSize = 36.sp,
                lineHeight = 39.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = (-0.5).sp,
                modifier = Modifier.padding(top = 12.dp),
            )
            Text(
                "Track plans, reminders, diary moments, hobbies, learning, goals and the projects you’re building — all in one thoughtful system.",
                fontSize = 15.sp,
                lineHeight = 23.sp,
                color = Color(0xFF6A665D),
                modifier = Modifier.padding(top = 18.dp),
            )

            Surface(
                modifier = Modifier.fillMaxWidth().padding(top = 28.dp),
                shape = RoundedCornerShape(24.dp),
                color = CardCream,
                shadowElevation = 3.dp,
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text("What this app helps you do", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Text(
                        "Capture anything quickly, see what matters today, remember meaningful moments, and gradually understand how your life is actually moving.",
                        fontSize = 13.sp,
                        lineHeight = 19.sp,
                        color = Color(0xFF6F695F),
                        modifier = Modifier.padding(top = 8.dp),
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(top = 12.dp)) {
                        OnboardChip("Today"); OnboardChip("Plan"); OnboardChip("Capture")
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(top = 8.dp)) {
                        OnboardChip("Journey"); OnboardChip("Me")
                    }
                }
            }

            Surface(
                modifier = Modifier.fillMaxWidth().padding(top = 18.dp),
                shape = RoundedCornerShape(20.dp),
                color = MossSoft,
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("What matters most to you right now?", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    ChoiceRow(listOf("Projects", "Diary", "Reminders"), chosen.value) { label -> chosen.value = chosen.value.toggle(label) }
                    ChoiceRow(listOf("Habits", "Learning", "Hobbies"), chosen.value) { label -> chosen.value = chosen.value.toggle(label) }
                    ChoiceRow(listOf("Goals"), chosen.value) { label -> chosen.value = chosen.value.toggle(label) }
                }
            }

            Surface(
                modifier = Modifier.fillMaxWidth().padding(top = 14.dp),
                shape = RoundedCornerShape(20.dp),
                color = LavenderSoft,
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Pick a few current life areas", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.padding(top = 12.dp)) {
                        ChoicePill("Game Development", true, Modifier.weight(1.2f), null)
                        ChoicePill("App Building", false, Modifier.weight(0.8f), null)
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.padding(top = 10.dp)) {
                        ChoicePill("Filmmaking", false, Modifier.weight(1f), null)
                        ChoicePill("Health", false, Modifier.weight(0.7f), null)
                        ChoicePill("Reading", false, Modifier.weight(0.8f), null)
                    }
                }
            }

            Row(modifier = Modifier.fillMaxWidth().padding(top = 24.dp), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedButton(
                    onClick = onSkip,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(18.dp),
                ) { Text("Maybe later", fontSize = 13.sp, fontWeight = FontWeight.Bold) }
                Button(
                    onClick = onContinue,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(18.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Moss),
                ) { Text("Continue", fontSize = 13.sp, fontWeight = FontWeight.Bold) }
            }
            Text(
                "Private by default · Built to support your life, not judge it",
                fontSize = 11.sp,
                color = Color(0xFF7B766D),
                modifier = Modifier.fillMaxWidth().padding(top = 18.dp, bottom = 10.dp),
            )
        }
    }
}

private fun Set<String>.toggle(label: String): Set<String> = if (contains(label)) this - label else this + label

@Composable
private fun OnboardChip(label: String) {
    Text(label, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF5F725A), modifier = Modifier.background(MossSoft, RoundedCornerShape(16.dp)).padding(horizontal = 11.dp, vertical = 8.dp))
}

@Composable
private fun ChoiceRow(labels: List<String>, selected: Set<String>, onToggle: (String) -> Unit) {
    Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.padding(top = 10.dp)) {
        labels.forEach { label -> ChoicePill(label, selected.contains(label), Modifier.weight(1f), { onToggle(label) }) }
    }
}

@Composable
private fun ChoicePill(label: String, selected: Boolean, modifier: Modifier, onClick: (() -> Unit)?) {
    Surface(
        onClick = onClick ?: {},
        enabled = onClick != null,
        modifier = modifier,
        shape = RoundedCornerShape(17.dp),
        color = if (selected) Color(0xFFD8E1D5) else Color(0xFFF1ECE3),
        border = null,
    ) {
        Box(modifier = Modifier.padding(horizontal = 10.dp, vertical = 9.dp), contentAlignment = Alignment.Center) {
            Text(label, fontSize = 12.sp, lineHeight = 15.sp, fontWeight = FontWeight.Bold, maxLines = 2)
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

    Column(
        modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background).verticalScroll(rememberScrollState()).padding(horizontal = 20.dp, vertical = 26.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Text("PERMISSIONS & RELIABILITY", fontSize = 11.sp, letterSpacing = 1.3.sp, color = Moss, fontWeight = FontWeight.Bold)
        Text("Let the important parts work well", fontSize = 32.sp, lineHeight = 35.sp, fontWeight = FontWeight.ExtraBold)
        Text("You can continue without optional permissions. We’ll always show clearly when a feature is limited.", fontSize = 14.sp, lineHeight = 20.sp, color = InkMuted)

        PermissionCard(Icons.Outlined.Notifications, "Notifications", "Needed for reminders and follow-ups.", if (notificationsGranted) "Allowed" else "Allow") {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) notificationLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
        PermissionCard(Icons.Outlined.Alarm, "Exact alarms", "Recommended for reminders that must fire at the exact time.", if (exactGranted) "Ready" else "Open settings") {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                context.startActivity(Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM, Uri.parse("package:${context.packageName}")))
            }
        }
        PermissionCard(Icons.Outlined.Mic, "Microphone", "Optional. Enables voice capture.", if (micGranted) "Allowed" else "Allow") {
            micLauncher.launch(Manifest.permission.RECORD_AUDIO)
        }
        Spacer(Modifier.size(8.dp))
        Button(onClick = onFinish, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(18.dp), colors = ButtonDefaults.buttonColors(containerColor = Moss)) {
            Text("Finish setup", fontWeight = FontWeight.Bold)
        }
        Text("You stay in control. Permissions can be changed anytime in settings.", fontSize = 11.sp, color = InkMuted)
    }
}

@Composable
private fun PermissionCard(icon: androidx.compose.ui.graphics.vector.ImageVector, title: String, body: String, state: String, onClick: () -> Unit) {
    Surface(onClick = onClick, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(20.dp), color = CardCream) {
        Row(modifier = Modifier.padding(16.dp), horizontalArrangement = Arrangement.spacedBy(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(42.dp).background(MossSoft, RoundedCornerShape(16.dp)), contentAlignment = Alignment.Center) { Icon(icon, contentDescription = null, tint = Moss) }
            Column(modifier = Modifier.weight(1f)) {
                Text(title, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                Text(body, fontSize = 12.sp, lineHeight = 16.sp, color = InkMuted, modifier = Modifier.padding(top = 4.dp))
            }
            Text(state, fontSize = 11.sp, color = Moss, fontWeight = FontWeight.Bold)
        }
    }
}
