package com.navin.personallifeos.ui.screens

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.navin.personallifeos.ui.theme.GoldSoft
import com.navin.personallifeos.ui.theme.InkMuted
import com.navin.personallifeos.ui.theme.LavenderSoft
import com.navin.personallifeos.ui.theme.MossSoft
import com.navin.personallifeos.ui.viewmodel.HomeViewModel

@Composable
fun MeScreen(viewModel: HomeViewModel = hiltViewModel()) {
    val tasks by viewModel.allTasks.collectAsState()
    val projects by viewModel.projects.collectAsState()
    val journal by viewModel.journal.collectAsState()
    val ideas by viewModel.ideas.collectAsState()
    val completed = tasks.count { it.completedAt != null }

    ScreenColumn {
        Eyebrow("Me")
        PageTitle("Navin")
        Text(
            "Creator · Developer · Learner",
            style = MaterialTheme.typography.titleMedium,
            color = InkMuted,
        )

        AccentCard(
            eyebrow = "Current chapter",
            title = "Building more than consuming",
            body = "Turning ideas into real products, improving your craft, and leaving a clearer record of the journey.",
            containerColor = LavenderSoft,
        )

        SectionTitle("What you’re building")
        if (projects.isEmpty()) {
            EmptyCard("No active projects yet", "Your projects will become part of this evolving identity page.")
        } else {
            projects.take(3).forEach { project ->
                WarmCard(
                    project.title,
                    project.currentMilestone.ifBlank { "Active project" },
                )
            }
        }

        SectionTitle("Your living record")
        AccentCard(
            eyebrow = "Progress evidence",
            title = "$completed tasks completed",
            body = "${tasks.count { it.completedAt == null }} still open · ${journal.size} diary entries · ${ideas.size} ideas captured",
            containerColor = MossSoft,
        )

        AccentCard(
            eyebrow = "Recent achievement",
            title = "You’re building a system that remembers",
            body = "Small technical and creative wins will live here as evidence of growth, not as artificial points.",
            containerColor = GoldSoft,
        )

        WarmCard(
            "Privacy",
            "Local-first foundation. Your V1 data stays on this device unless you explicitly choose backup or sync later.",
        )
    }
}
