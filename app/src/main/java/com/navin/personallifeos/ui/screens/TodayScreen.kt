package com.navin.personallifeos.ui.screens

import androidx.compose.foundation.layout.Arrangement
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
import com.navin.personallifeos.ui.viewmodel.HomeViewModel
import java.text.DateFormat
import java.util.Date

@Composable
fun TodayScreen(viewModel: HomeViewModel = hiltViewModel()) {
    val tasks by viewModel.pendingTasks.collectAsState()
    val activity by viewModel.activity.collectAsState()
    val journal by viewModel.journal.collectAsState()

    ScreenColumn {
        Text("TODAY", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
        Text("Your day, gently focused", style = MaterialTheme.typography.displaySmall)
        Text(DateFormat.getDateInstance(DateFormat.FULL).format(Date()), style = MaterialTheme.typography.bodyMedium)

        val focus = tasks.firstOrNull()
        if (focus != null) {
            WarmCard("Today’s focus", focus.title)
        } else {
            WarmCard("Today’s focus", "Nothing urgent. Capture what matters or choose one small thing worth moving forward.")
        }

        SectionTitle("Your tasks")
        if (tasks.isEmpty()) {
            EmptyCard("No open tasks", "Use Capture to add a task or reminder in natural language.")
        } else {
            tasks.take(4).forEach { task -> TaskRow(task, onComplete = { viewModel.completeTask(task) }) }
        }

        SectionTitle("Recent story")
        if (activity.isEmpty()) {
            EmptyCard("Your timeline starts here", "Completed tasks, ideas and diary entries will begin building your personal history.")
        } else {
            activity.take(2).forEach { event -> WarmCard(event.type.replace('_', ' ').replaceFirstChar { it.uppercase() }, event.title) }
        }

        WarmCard(
            "Evening reflection",
            journal.firstOrNull()?.body?.take(140) ?: "What felt most meaningful about today?",
        )
    }
}

@Composable
fun TaskRow(task: TaskEntity, onComplete: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 1.dp,
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Checkbox(checked = false, onCheckedChange = { if (it) onComplete() })
            androidx.compose.foundation.layout.Column(modifier = Modifier.weight(1f)) {
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
