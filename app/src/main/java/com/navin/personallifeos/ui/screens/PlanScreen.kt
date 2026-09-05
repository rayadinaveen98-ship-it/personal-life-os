package com.navin.personallifeos.ui.screens

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.navin.personallifeos.ui.theme.GoldSoft
import com.navin.personallifeos.ui.theme.InkMuted
import com.navin.personallifeos.ui.theme.MossSoft
import com.navin.personallifeos.ui.viewmodel.HomeViewModel

@Composable
fun PlanScreen(viewModel: HomeViewModel = hiltViewModel()) {
    val tasks by viewModel.pendingTasks.collectAsState()
    val projects by viewModel.projects.collectAsState()

    ScreenColumn {
        Eyebrow("Plan")
        PageTitle("A gentle direction for what comes next")
        Text(
            "Plan around meaning, not just a longer list.",
            style = MaterialTheme.typography.bodyMedium,
            color = InkMuted,
        )
        MetaRow("Today", "Week", "Projects")

        val focus = tasks.firstOrNull()
        AccentCard(
            eyebrow = "Priority focus",
            title = focus?.title ?: "Choose one thing worth moving",
            body = if (focus == null) {
                "Your week can begin with a single next action."
            } else {
                "This is the clearest next action in your current plan."
            },
            containerColor = MossSoft,
        )

        SectionTitle("Today’s tasks")
        if (tasks.isEmpty()) {
            EmptyCard("Nothing planned yet", "Capture a task or reminder and it will appear here automatically.")
        } else {
            tasks.take(5).forEach { task -> TaskRow(task, onComplete = { viewModel.completeTask(task) }) }
        }

        SectionTitle("Active projects")
        if (projects.isEmpty()) {
            EmptyCard("No projects yet", "Projects connect small tasks to outcomes that actually matter to you.")
        } else {
            projects.take(3).forEach { project ->
                WarmCard(
                    project.title,
                    project.currentMilestone.ifBlank { project.description.ifBlank { "Active project" } },
                )
            }
        }

        AccentCard(
            eyebrow = "Weekly review",
            title = "Your week in perspective",
            body = "What moved forward? What was ignored? What deserves protected time next week?",
            containerColor = GoldSoft,
        )
    }
}
