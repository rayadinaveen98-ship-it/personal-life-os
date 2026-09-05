package com.navin.personallifeos.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.navin.personallifeos.data.local.TaskEntity
import com.navin.personallifeos.ui.theme.CardCream
import com.navin.personallifeos.ui.theme.GoldSoft
import com.navin.personallifeos.ui.theme.LavenderSoft
import com.navin.personallifeos.ui.theme.Moss
import com.navin.personallifeos.ui.theme.MossSoft
import com.navin.personallifeos.ui.theme.SoftLavender
import com.navin.personallifeos.ui.theme.Terracotta
import com.navin.personallifeos.ui.theme.TerracottaSoft
import com.navin.personallifeos.ui.viewmodel.HomeViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun TodayScreen(
    onOpenPlan: () -> Unit = {},
    onOpenCapture: () -> Unit = {},
    onOpenJourney: () -> Unit = {},
    onOpenTask: (String) -> Unit = {},
    onOpenProject: (String) -> Unit = {},
    onOpenJournal: (String) -> Unit = {},
    onOpenIdea: (String) -> Unit = {},
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val pending by viewModel.pendingTasks.collectAsState()
    val allTasks by viewModel.allTasks.collectAsState()
    val projects by viewModel.projects.collectAsState()
    val journal by viewModel.journal.collectAsState()
    val activity by viewModel.activity.collectAsState()
    val preferredName by viewModel.preferredName.collectAsState()
    val focusAreas by viewModel.focusAreas.collectAsState()
    val lifeAreas by viewModel.lifeAreas.collectAsState()
    val eveningReflection by viewModel.eveningReflection.collectAsState()

    val now = Calendar.getInstance()
    val greeting = when (now.get(Calendar.HOUR_OF_DAY)) {
        in 5..11 -> "Good morning"
        in 12..16 -> "Good afternoon"
        else -> "Good evening"
    }
    val displayName = preferredName.trim().ifBlank { "Navin" }
    val focus = pending.sortedWith(
        compareByDescending<TaskEntity> { it.priority }
            .thenBy { it.dueAt ?: Long.MAX_VALUE }
            .thenBy { it.createdAt },
    ).firstOrNull()
    val focusProject = projects.firstOrNull { it.id == focus?.projectId }
    val nextReminder = pending.filter { it.reminderAt != null }.minByOrNull { it.reminderAt ?: Long.MAX_VALUE }
    val completedToday = allTasks.count { task -> task.completedAt?.let(::isToday) == true }
    val dueToday = pending.count { task -> task.dueAt?.let(::isToday) == true }
    val recentActivity = activity.firstOrNull()
    val latestJournal = journal.firstOrNull()
    val activeArea = focusProject?.title ?: lifeAreas.sorted().firstOrNull() ?: focusAreas.sorted().firstOrNull() ?: "Your life"

    fun openFocus() {
        if (focus != null) onOpenTask(focus.id) else onOpenCapture()
    }

    fun openRecentStory() {
        val event = recentActivity
        val id = event?.entityId
        if (event != null && id != null) {
            when {
                event.type.startsWith("task") || event.type.startsWith("reminder") -> onOpenTask(id)
                event.type == "journal" -> onOpenJournal(id)
                event.type == "idea" -> onOpenIdea(id)
                else -> onOpenJourney()
            }
        } else if (latestJournal != null) {
            onOpenJournal(latestJournal.id)
        } else {
            onOpenJourney()
        }
    }

    Column(
        modifier = Modifier.verticalScroll(rememberScrollState()).padding(start = 20.dp, end = 20.dp, top = 14.dp, bottom = 20.dp),
    ) {
        Text("YOUR DAY", fontSize = 12.sp, color = Color(0xFF737267), letterSpacing = 0.6.sp)
        Text("$greeting,\n$displayName.", fontSize = 30.sp, lineHeight = 31.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = (-0.7).sp, modifier = Modifier.padding(top = 5.dp))
        Text(SimpleDateFormat("EEEE, MMMM d", Locale.getDefault()).format(Date()), fontSize = 13.sp, color = Color(0xFF767469), modifier = Modifier.padding(top = 7.dp))

        if (lifeAreas.isNotEmpty()) {
            Row(modifier = Modifier.padding(top = 14.dp), horizontalArrangement = Arrangement.spacedBy(7.dp)) {
                lifeAreas.sorted().take(2).forEach { area ->
                    Text(area, fontSize = 10.5.sp, color = Moss, fontWeight = FontWeight.Bold, modifier = Modifier.background(MossSoft, RoundedCornerShape(999.dp)).padding(horizontal = 9.dp, vertical = 6.dp))
                }
                if (lifeAreas.size > 2) {
                    Text("+${lifeAreas.size - 2}", fontSize = 10.5.sp, color = Color(0xFF77736A), fontWeight = FontWeight.Bold, modifier = Modifier.background(CardCream, RoundedCornerShape(999.dp)).padding(horizontal = 9.dp, vertical = 6.dp))
                }
            }
        }

        Row(modifier = Modifier.fillMaxWidth().padding(top = 22.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text("Today’s focus", fontSize = 14.sp, fontWeight = FontWeight.ExtraBold)
            Surface(onClick = ::openFocus, color = Color.Transparent) {
                Text(if (focus == null) "Add one" else "Open task", fontSize = 11.sp, color = Moss, fontWeight = FontWeight.Bold, modifier = Modifier.padding(4.dp))
            }
        }

        Surface(onClick = ::openFocus, modifier = Modifier.fillMaxWidth().padding(top = 10.dp), shape = RoundedCornerShape(24.dp), color = Moss) {
            Box(modifier = Modifier.height(150.dp)) {
                Box(modifier = Modifier.size(126.dp).offset(x = 255.dp, y = (-26).dp).border(1.dp, Color.White.copy(alpha = 0.18f), CircleShape))
                Box(modifier = Modifier.size(78.dp).offset(x = 250.dp, y = 34.dp).border(1.dp, Color.White.copy(alpha = 0.12f), CircleShape))
                Column(modifier = Modifier.padding(19.dp)) {
                    Text("PRIORITY · ${focusProject?.title?.uppercase() ?: activeArea.uppercase()}", fontSize = 11.sp, letterSpacing = 1.sp, fontWeight = FontWeight.Bold, color = Color.White.copy(alpha = 0.78f), maxLines = 1)
                    Text(
                        focus?.title ?: "Choose one meaningful thing to move forward",
                        fontSize = 24.sp,
                        lineHeight = 26.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = (-0.4).sp,
                        color = Color.White,
                        modifier = Modifier.fillMaxWidth(0.78f).padding(top = 11.dp),
                        maxLines = 2,
                    )
                    Row(modifier = Modifier.padding(top = 14.dp), horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                        Text(if (pending.isEmpty()) "Capture first action" else "${pending.size} open actions", fontSize = 11.sp, color = Color.White, modifier = Modifier.background(Color.White.copy(alpha = 0.13f), RoundedCornerShape(999.dp)).padding(horizontal = 10.dp, vertical = 7.dp))
                        Text(if (dueToday > 0) "$dueToday due today" else if (completedToday > 0) "$completedToday finished today" else "Keep it light", fontSize = 11.sp, color = Color.White.copy(alpha = 0.92f))
                    }
                }
            }
        }

        Row(modifier = Modifier.fillMaxWidth().padding(top = 23.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text("Keep moving", fontSize = 14.sp, fontWeight = FontWeight.ExtraBold)
            Surface(onClick = onOpenPlan, color = Color.Transparent) { Text("See plan", fontSize = 11.sp, color = Moss, fontWeight = FontWeight.Bold, modifier = Modifier.padding(4.dp)) }
        }

        Column(modifier = Modifier.padding(top = 10.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                TodayMiniCard(Modifier.weight(1f), onOpenPlan, "✓", MossSoft, Moss, "Tasks", if (completedToday > 0) "$completedToday done today" else "${pending.size} open actions", if (dueToday > 0) "$dueToday due today" else "Open your plan")
                TodayMiniCard(
                    Modifier.weight(1f),
                    onClick = { if (focusProject != null) onOpenProject(focusProject.id) else onOpenPlan() },
                    icon = "◎",
                    iconBg = GoldSoft,
                    iconColor = Color(0xFF9D6C28),
                    label = if (focusProject != null) "Project" else "Life area",
                    title = activeArea,
                    meta = if (focusProject != null) focusProject.currentMilestone.ifBlank { "Open project" } else "From your setup",
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                TodayMiniCard(
                    Modifier.weight(1f),
                    onClick = ::openRecentStory,
                    icon = "↗",
                    iconBg = LavenderSoft,
                    iconColor = Color(0xFF75698A),
                    label = "Recent story",
                    title = recentActivity?.title ?: latestJournal?.title ?: "Your story starts here",
                    meta = recentActivity?.let { activityLabel(it.type, it.occurredAt) } ?: "Open Journey",
                )
                TodayMiniCard(
                    Modifier.weight(1f),
                    onClick = { if (nextReminder != null) onOpenTask(nextReminder.id) else onOpenCapture() },
                    icon = "◷",
                    iconBg = TerracottaSoft,
                    iconColor = Terracotta,
                    label = "Reminder",
                    title = nextReminder?.title?.take(28) ?: "Add a reminder",
                    meta = nextReminder?.reminderAt?.let { reminderLabel(it) } ?: "Capture it naturally",
                )
            }
        }

        if (eveningReflection) {
            Surface(onClick = onOpenCapture, modifier = Modifier.fillMaxWidth().padding(top = 12.dp), shape = RoundedCornerShape(22.dp), color = LavenderSoft) {
                Row(modifier = Modifier.padding(16.dp), horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.Top) {
                    Box(modifier = Modifier.size(32.dp).clip(CircleShape).background(SoftLavender), contentAlignment = Alignment.Center) { Text("☾", color = Color.White, fontSize = 15.sp) }
                    Column {
                        Text("What was worth remembering today?", fontSize = 13.sp, lineHeight = 18.sp, fontWeight = FontWeight.Bold)
                        Text("Tap to capture tonight’s reflection", fontSize = 10.5.sp, color = Color(0xFF777081), modifier = Modifier.padding(top = 4.dp))
                    }
                }
            }
        }
        Spacer(Modifier.height(8.dp))
    }
}

@Composable
private fun TodayMiniCard(modifier: Modifier, onClick: () -> Unit, icon: String, iconBg: Color, iconColor: Color, label: String, title: String, meta: String) {
    Surface(onClick = onClick, modifier = modifier.height(112.dp), shape = RoundedCornerShape(20.dp), color = CardCream, shadowElevation = 1.dp) {
        Column(modifier = Modifier.padding(15.dp)) {
            Box(modifier = Modifier.size(28.dp).clip(RoundedCornerShape(9.dp)).background(iconBg), contentAlignment = Alignment.Center) { Text(icon, fontSize = 14.sp, color = iconColor) }
            Text(label, fontSize = 11.sp, color = Color(0xFF77756B), modifier = Modifier.padding(top = 8.dp))
            Text(title, fontSize = 15.sp, lineHeight = 17.sp, fontWeight = FontWeight.ExtraBold, maxLines = 2)
            Text(meta, fontSize = 10.5.sp, color = Color(0xFF87857B), modifier = Modifier.padding(top = 6.dp), maxLines = 1)
        }
    }
}

private fun isToday(time: Long): Boolean {
    val now = Calendar.getInstance()
    val target = Calendar.getInstance().apply { timeInMillis = time }
    return now.get(Calendar.YEAR) == target.get(Calendar.YEAR) && now.get(Calendar.DAY_OF_YEAR) == target.get(Calendar.DAY_OF_YEAR)
}

private fun activityLabel(type: String, time: Long): String {
    val label = when (type) {
        "task_completed" -> "Task completed"
        "task_reopened" -> "Task reopened"
        "task_created" -> "Task added"
        "reminder_created" -> "Reminder added"
        "journal" -> "Journal entry"
        "idea" -> "Idea captured"
        "activity_log" -> "Activity logged"
        else -> type.replace('_', ' ').replaceFirstChar { it.uppercase() }
    }
    return "$label · ${relativeTime(time)}"
}

private fun relativeTime(time: Long): String {
    val minutes = ((System.currentTimeMillis() - time).coerceAtLeast(0L) / 60_000L).toInt()
    return when {
        minutes < 1 -> "just now"
        minutes < 60 -> "${minutes}m ago"
        minutes < 1_440 -> "${minutes / 60}h ago"
        minutes < 2_880 -> "yesterday"
        else -> SimpleDateFormat("MMM d", Locale.getDefault()).format(Date(time))
    }
}

private fun reminderLabel(time: Long): String {
    val now = Calendar.getInstance()
    val target = Calendar.getInstance().apply { timeInMillis = time }
    val tomorrow = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, 1) }
    val prefix = when {
        target.get(Calendar.YEAR) == tomorrow.get(Calendar.YEAR) && target.get(Calendar.DAY_OF_YEAR) == tomorrow.get(Calendar.DAY_OF_YEAR) -> "Tomorrow"
        target.get(Calendar.YEAR) == now.get(Calendar.YEAR) && target.get(Calendar.DAY_OF_YEAR) == now.get(Calendar.DAY_OF_YEAR) -> "Today"
        else -> SimpleDateFormat("MMM d", Locale.getDefault()).format(Date(time))
    }
    return "$prefix · ${SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date(time))}"
}
