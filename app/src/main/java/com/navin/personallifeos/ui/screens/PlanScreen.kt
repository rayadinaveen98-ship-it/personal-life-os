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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.navin.personallifeos.data.local.ProjectEntity
import com.navin.personallifeos.data.local.TaskEntity
import com.navin.personallifeos.ui.theme.CardCream
import com.navin.personallifeos.ui.theme.GoldSoft
import com.navin.personallifeos.ui.theme.LavenderSoft
import com.navin.personallifeos.ui.theme.Moss
import com.navin.personallifeos.ui.theme.MossSoft
import com.navin.personallifeos.ui.viewmodel.HomeViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun PlanScreen(
    onOpenCapture: () -> Unit = {},
    onOpenTask: (String) -> Unit = {},
    onOpenProject: (String?) -> Unit = {},
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val pending by viewModel.pendingTasks.collectAsState()
    val allTasks by viewModel.allTasks.collectAsState()
    val projects by viewModel.projects.collectAsState()
    val preferredName by viewModel.preferredName.collectAsState()
    var selectedTab by rememberSaveable { mutableIntStateOf(0) }

    val focus = pending.sortedWith(
        compareByDescending<TaskEntity> { it.priority }
            .thenBy { it.dueAt ?: Long.MAX_VALUE }
            .thenBy { it.createdAt },
    ).firstOrNull()
    val completed = allTasks.count { it.completedAt != null }

    Column(
        modifier = Modifier.verticalScroll(rememberScrollState()).padding(start = 20.dp, end = 20.dp, top = 18.dp, bottom = 20.dp),
    ) {
        Text("PERSONAL LIFE OS", fontSize = 12.sp, letterSpacing = 1.7.sp, color = Color(0xFF7F7B71), fontWeight = FontWeight.Bold)
        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom,
        ) {
            Column {
                Text("Plan", fontSize = 34.sp, lineHeight = 34.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = (-0.5).sp)
                Text(SimpleDateFormat("EEEE, MMMM d", Locale.getDefault()).format(Date()), fontSize = 12.sp, color = Color(0xFF747066), modifier = Modifier.padding(top = 7.dp))
            }
            Box(modifier = Modifier.size(40.dp).clip(CircleShape).background(Moss), contentAlignment = Alignment.Center) {
                Text(preferredName.trim().firstOrNull()?.uppercaseChar()?.toString() ?: "N", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 20.dp).background(Color(0xFFE8E3D8), RoundedCornerShape(18.dp)).padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            PlanSegment("Today", selectedTab == 0, { selectedTab = 0 }, Modifier.weight(1f))
            PlanSegment("Week", selectedTab == 1, { selectedTab = 1 }, Modifier.weight(1f))
            PlanSegment("Projects", selectedTab == 2, { selectedTab = 2 }, Modifier.weight(1f))
        }

        when (selectedTab) {
            0 -> TodayPlanContent(
                pending = pending,
                allTasks = allTasks,
                projects = projects,
                focus = focus,
                completed = completed,
                onOpenCapture = onOpenCapture,
                onOpenTask = onOpenTask,
                onToggleTask = viewModel::toggleTask,
            )
            1 -> WeekPlanContent(
                tasks = pending,
                onOpenTask = onOpenTask,
                onToggleTask = viewModel::toggleTask,
                onOpenCapture = onOpenCapture,
            )
            else -> ProjectsPlanContent(
                projects = projects,
                tasks = allTasks,
                onOpenProject = onOpenProject,
            )
        }
        Spacer(Modifier.height(8.dp))
    }
}

@Composable
private fun TodayPlanContent(
    pending: List<TaskEntity>,
    allTasks: List<TaskEntity>,
    projects: List<ProjectEntity>,
    focus: TaskEntity?,
    completed: Int,
    onOpenCapture: () -> Unit,
    onOpenTask: (String) -> Unit,
    onToggleTask: (TaskEntity) -> Unit,
) {
    val focusProject = projects.firstOrNull { it.id == focus?.projectId }
    Surface(
        onClick = { if (focus != null) onOpenTask(focus.id) else onOpenCapture() },
        modifier = Modifier.fillMaxWidth().padding(top = 18.dp),
        shape = RoundedCornerShape(24.dp),
        color = Moss,
        shadowElevation = 4.dp,
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("PRIORITY FOCUS", fontSize = 11.sp, letterSpacing = 1.3.sp, fontWeight = FontWeight.Bold, color = Color.White.copy(alpha = 0.82f))
                Text("$completed of ${allTasks.size} done", fontSize = 11.sp, color = Color.White, modifier = Modifier.background(Color.White.copy(alpha = 0.16f), RoundedCornerShape(999.dp)).padding(horizontal = 9.dp, vertical = 6.dp))
            }
            Text(focus?.title ?: "Choose one meaningful push", fontSize = 20.sp, lineHeight = 24.sp, fontWeight = FontWeight.Bold, color = Color.White, modifier = Modifier.padding(top = 12.dp))
            Text(
                focusProject?.let { "${it.title}${it.currentMilestone.takeIf(String::isNotBlank)?.let { milestone -> " · $milestone" } ?: ""}" }
                    ?: if (focus == null) "Tap to capture your first action." else taskTimingSummary(focus),
                fontSize = 13.sp,
                lineHeight = 18.sp,
                color = Color(0xFFEEF2EB),
                modifier = Modifier.padding(top = 8.dp),
            )
            Box(modifier = Modifier.fillMaxWidth().padding(top = 14.dp).height(7.dp).clip(RoundedCornerShape(999.dp)).background(Color.White.copy(alpha = 0.18f))) {
                val progress = if (allTasks.isEmpty()) 0f else (completed.toFloat() / allTasks.size.toFloat()).coerceIn(0f, 1f)
                Box(modifier = Modifier.fillMaxWidth(progress).height(7.dp).background(Color(0xFFF3EEE5), RoundedCornerShape(999.dp)))
            }
        }
    }

    SectionHeader("Today", "+ Add task", onOpenCapture)
    if (pending.isEmpty()) {
        PlanEmptyCompact("Nothing planned yet", "Use Capture to add your first task or reminder.")
    } else {
        Column(verticalArrangement = Arrangement.spacedBy(9.dp)) {
            pending.forEach { task ->
                PlanTaskRow(task, onOpen = { onOpenTask(task.id) }, onToggle = { onToggleTask(task) })
            }
        }
    }

    val comingUp = pending
        .filter { task -> (task.reminderAt ?: task.dueAt)?.let { it > System.currentTimeMillis() } == true }
        .sortedBy { it.reminderAt ?: it.dueAt ?: Long.MAX_VALUE }
        .take(2)

    SectionHeader("Coming up", null, null)
    if (comingUp.isEmpty()) {
        PlanEmptyCompact("Your horizon is clear", "Future due dates and reminders will appear here.")
    } else {
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            comingUp.forEachIndexed { index, task ->
                ComingUpCard(
                    modifier = Modifier.weight(1f),
                    color = if (index == 0) LavenderSoft else GoldSoft,
                    task = task,
                    onClick = { onOpenTask(task.id) },
                )
            }
        }
    }
}

@Composable
private fun WeekPlanContent(
    tasks: List<TaskEntity>,
    onOpenTask: (String) -> Unit,
    onToggleTask: (TaskEntity) -> Unit,
    onOpenCapture: () -> Unit,
) {
    val todayStart = startOfDay(System.currentTimeMillis())
    Text("NEXT 7 DAYS", fontSize = 11.sp, letterSpacing = 1.3.sp, color = Color(0xFF777166), fontWeight = FontWeight.ExtraBold, modifier = Modifier.padding(top = 20.dp, bottom = 4.dp))
    var anyScheduled = false
    repeat(7) { offset ->
        val dayStart = todayStart + offset * 86_400_000L
        val dayEnd = dayStart + 86_400_000L
        val dayTasks = tasks.filter { task ->
            val time = task.dueAt ?: task.reminderAt
            time != null && time in dayStart until dayEnd
        }
        if (dayTasks.isNotEmpty()) {
            anyScheduled = true
            val label = if (offset == 0) "Today" else SimpleDateFormat("EEE, MMM d", Locale.getDefault()).format(Date(dayStart))
            Text(label, fontSize = 14.sp, fontWeight = FontWeight.ExtraBold, modifier = Modifier.padding(top = 14.dp, bottom = 8.dp))
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                dayTasks.forEach { task -> PlanTaskRow(task, { onOpenTask(task.id) }, { onToggleTask(task) }) }
            }
        }
    }
    if (!anyScheduled) PlanEmptyCompact("No dated actions this week", "Add a due date or reminder through Capture to place work on the week.")

    val unscheduled = tasks.filter { it.dueAt == null && it.reminderAt == null }
    if (unscheduled.isNotEmpty()) {
        SectionHeader("Unscheduled", "+ Add", onOpenCapture)
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            unscheduled.forEach { task -> PlanTaskRow(task, { onOpenTask(task.id) }, { onToggleTask(task) }) }
        }
    }
}

@Composable
private fun ProjectsPlanContent(
    projects: List<ProjectEntity>,
    tasks: List<TaskEntity>,
    onOpenProject: (String?) -> Unit,
) {
    SectionHeader("Projects", "+ New project", { onOpenProject(null) })
    if (projects.isEmpty()) {
        PlanEmptyCompact("No projects yet", "Create a project first; Capture can then automatically link phrases like “work on CINEMA”.")
    } else {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            projects.forEach { project ->
                val projectTasks = tasks.filter { it.projectId == project.id }
                val open = projectTasks.count { it.completedAt == null }
                Surface(onClick = { onOpenProject(project.id) }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(20.dp), color = CardCream, shadowElevation = 1.dp) {
                    Column(modifier = Modifier.padding(15.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(project.title, fontSize = 15.sp, fontWeight = FontWeight.ExtraBold, modifier = Modifier.weight(1f))
                            Text("$open open", fontSize = 10.5.sp, color = Moss, fontWeight = FontWeight.Bold)
                        }
                        if (project.description.isNotBlank()) Text(project.description, fontSize = 11.sp, lineHeight = 16.sp, color = Color(0xFF777166), modifier = Modifier.padding(top = 5.dp), maxLines = 2)
                        if (project.currentMilestone.isNotBlank()) Text("Next · ${project.currentMilestone}", fontSize = 10.5.sp, color = Color(0xFF8A6D3B), modifier = Modifier.padding(top = 8.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun PlanSegment(label: String, active: Boolean, onClick: () -> Unit, modifier: Modifier) {
    Surface(onClick = onClick, modifier = modifier, shape = RoundedCornerShape(14.dp), color = if (active) CardCream else Color.Transparent, shadowElevation = if (active) 1.dp else 0.dp) {
        Box(modifier = Modifier.padding(vertical = 10.dp), contentAlignment = Alignment.Center) {
            Text(label, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = if (active) Color(0xFF2B2B27) else Color(0xFF777267))
        }
    }
}

@Composable
private fun SectionHeader(title: String, action: String?, onAction: (() -> Unit)?) {
    Row(modifier = Modifier.fillMaxWidth().padding(start = 2.dp, end = 2.dp, top = 19.dp, bottom = 10.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        Text(title, fontSize = 16.sp, fontWeight = FontWeight.ExtraBold)
        if (action != null && onAction != null) {
            Surface(onClick = onAction, color = Color.Transparent) {
                Text(action, fontSize = 11.sp, color = Moss, fontWeight = FontWeight.Bold, modifier = Modifier.padding(5.dp))
            }
        }
    }
}

@Composable
private fun PlanTaskRow(task: TaskEntity, onOpen: () -> Unit, onToggle: () -> Unit) {
    Surface(onClick = onOpen, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(18.dp), color = CardCream, shadowElevation = 1.dp) {
        Row(modifier = Modifier.padding(13.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Surface(onClick = onToggle, modifier = Modifier.size(30.dp), shape = CircleShape, color = Color.Transparent) {
                Box(contentAlignment = Alignment.Center) {
                    Box(modifier = Modifier.size(22.dp).border(2.dp, if (task.completedAt == null) Color(0xFFA9A496) else Moss, CircleShape), contentAlignment = Alignment.Center) {
                        if (task.completedAt != null) Text("✓", color = Moss, fontSize = 12.sp, fontWeight = FontWeight.ExtraBold)
                    }
                }
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(task.title, fontSize = 13.sp, lineHeight = 16.sp, fontWeight = FontWeight.Bold, maxLines = 2)
                Text(taskTimingSummary(task), fontSize = 10.5.sp, color = Color(0xFF878174), modifier = Modifier.padding(top = 4.dp), maxLines = 1)
            }
            Text("›", fontSize = 20.sp, color = Color(0xFF938D82))
        }
    }
}

@Composable
private fun ComingUpCard(modifier: Modifier, color: Color, task: TaskEntity, onClick: () -> Unit) {
    Surface(onClick = onClick, modifier = modifier.height(112.dp), shape = RoundedCornerShape(20.dp), color = color) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(if (task.reminderAt != null) "⏰" else "↗", fontSize = 18.sp)
            Text(task.title, fontSize = 13.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 8.dp), maxLines = 2)
            Text(taskTimingSummary(task), fontSize = 10.5.sp, lineHeight = 14.sp, color = Color(0xFF5F5B54), modifier = Modifier.padding(top = 4.dp), maxLines = 2)
        }
    }
}

@Composable
private fun PlanEmptyCompact(title: String, body: String) {
    Surface(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(18.dp), color = CardCream) {
        Column(modifier = Modifier.padding(13.dp)) {
            Text(title, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            Text(body, fontSize = 11.sp, lineHeight = 15.sp, color = Color(0xFF777267), modifier = Modifier.padding(top = 4.dp))
        }
    }
}

private fun taskTimingSummary(task: TaskEntity): String {
    val due = task.dueAt
    val reminder = task.reminderAt
    return when {
        reminder != null -> "Reminder · ${SimpleDateFormat("EEE h:mm a", Locale.getDefault()).format(Date(reminder))}"
        due != null -> "Due · ${SimpleDateFormat("EEE MMM d, h:mm a", Locale.getDefault()).format(Date(due))}"
        task.priority > 0 -> "High priority"
        task.priority < 0 -> "Low priority"
        else -> "No date"
    }
}

private fun startOfDay(time: Long): Long = Calendar.getInstance().apply {
    timeInMillis = time
    set(Calendar.HOUR_OF_DAY, 0)
    set(Calendar.MINUTE, 0)
    set(Calendar.SECOND, 0)
    set(Calendar.MILLISECOND, 0)
}.timeInMillis
