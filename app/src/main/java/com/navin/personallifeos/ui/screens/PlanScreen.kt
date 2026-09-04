package com.navin.personallifeos.ui.screens

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.navin.personallifeos.ui.viewmodel.HomeViewModel

@Composable
fun PlanScreen(viewModel: HomeViewModel = hiltViewModel()) {
    val tasks by viewModel.pendingTasks.collectAsState()
    val projects by viewModel.projects.collectAsState()

    ScreenColumn {
        Text("PLAN", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
        Text("Gentle direction", style = MaterialTheme.typography.displaySmall)
        MetaRow("Today", "Week", "Projects")

        if (tasks.isEmpty()) {
            EmptyCard("Nothing planned yet", "Capture a task or reminder and it will appear here automatically.")
        } else {
            SectionTitle("Next actions")
            tasks.forEach { task -> TaskRow(task, onComplete = { viewModel.completeTask(task) }) }
        }

        SectionTitle("Active projects")
        if (projects.isEmpty()) {
            EmptyCard("No projects yet", "Projects will connect your tasks to bigger outcomes when you add them.")
        } else {
            projects.take(4).forEach { project ->
                WarmCard(project.title, project.currentMilestone.ifBlank { project.description.ifBlank { "Active project" } })
            }
        }
    }
}
