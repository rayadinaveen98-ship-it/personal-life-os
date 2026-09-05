package com.navin.personallifeos.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.navin.personallifeos.data.local.TaskEntity
import com.navin.personallifeos.ui.theme.InkMuted
import com.navin.personallifeos.ui.theme.LavenderSoft
import com.navin.personallifeos.ui.theme.MossSoft
import com.navin.personallifeos.ui.viewmodel.HomeViewModel
import java.text.DateFormat
import java.util.Calendar
import java.util.Date

@Composable
fun TodayScreen(viewModel: HomeViewModel = hiltViewModel()) {
    val tasks by viewModel.pendingTasks.collectAsState()
    val activity by viewModel.activity.collectAsState()
    val journal by viewModel.journal.collectAsState()
    val projects by viewModel.projects.collectAsState()

    val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
    val greeting = when (hour) {
        in 5..11 -> "Good morning, Navin"
        in 12..16 -> "Good afternoon, Navin"
        else -> "Good evening, Navin"
    }

    ScreenColumn {
        Eyebrow("Today")
        PageTitle(greeting)
        Text(
            DateFormat.getDateInstance(DateFormat.FULL).format(Date()),
            style = MaterialTheme.typography.bodyMedium,
            color = InkMuted,
        )

        val focus = tasks.firstOrNull()
        AccentCard(
            eyebrow = "Today’s focus",
            title = focus?.title ?: "A clear day is still a useful day",
            body = if (focus != null) {
                "One meaningful next action is enough. Finish this, then decide what deserves attention next."
            } else {
                "Nothing urgent. Capture what matters or choose one small thing worth moving forward."
            },
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
        )

        SectionTitle("Due & next")
        if (tasks.isEmpty()) {
            EmptyCard(
                "No open tasks",
                "Use the center Capture button to add a task or reminder in natural language.",
            )
        } else {
            tasks.take(3).forEach { task -> TaskRow(task, onComplete = { viewModel.completeTask(task) }) }
        }

        SectionTitle("Continue")
        val activeProject = projects.firstOrNull()
        if (activeProject == null) {
            AccentCard(
                eyebrow = "Projects",
                title = "Nothing active yet",
                body = "When you create a project, this area becomes your quick way back to where you stopped.",
                containerColor = MossSoft,
            )
        } else {
            AccentCard(
                eyebrow = "Active project",
                title = activeProject.title,
                body = activeProject.currentMilestone.ifBlank {
                    activeProject.description.ifBlank { "Pick up from your latest project activity." }
                },
                containerColor = MossSoft,
            )
        }

        SectionTitle("Recent story")
        if (activity.isEmpty()) {
            EmptyCard(
                "Your timeline starts here",
                "Completed tasks, ideas and diary entries will gradually build a personal history.",
            )
        } else {
            activity.take(2).forEach { event ->
                WarmCard(
                    event.type.replace('_', ' ').replaceFirstChar { it.uppercase() },
                    event.title,
                )
            }
        }

        AccentCard(
            eyebrow = "Evening reflection",
            title = "What was worth remembering today?",
            body = journal.firstOrNull()?.body?.take(160)
                ?: "A thought, a small win, a conversation, or simply how the day felt.",
            containerColor = LavenderSoft,
        )
    }
}

@Composable
fun TaskRow(task: TaskEntity, onComplete: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 1.dp,
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 13.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Checkbox(checked = false, onCheckedChange = { if (it) onComplete() })
            Column(modifier = Modifier.weight(1f)) {
                Text(task.title, style = MaterialTheme.typography.titleMedium)
                task.reminderAt?.let {
                    Text(
                        DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT).format(Date(it)),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                    )
                }
            }
        }
    }
}

@Composable
fun EmptyCard(title: String, body: String) = WarmCard(title, body)
