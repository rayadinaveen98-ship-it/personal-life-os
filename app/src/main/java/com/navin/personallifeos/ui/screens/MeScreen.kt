package com.navin.personallifeos.ui.screens

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.navin.personallifeos.ui.viewmodel.HomeViewModel

@Composable
fun MeScreen(viewModel: HomeViewModel = hiltViewModel()) {
    val tasks by viewModel.allTasks.collectAsState()
    val projects by viewModel.projects.collectAsState()
    val journal by viewModel.journal.collectAsState()
    val ideas by viewModel.ideas.collectAsState()
    val completed = tasks.count { it.completedAt != null }

    ScreenColumn {
        Text("ME", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
        Text("Creator · Developer · Learner", style = MaterialTheme.typography.displaySmall)
        WarmCard("Current chapter", "Building more than consuming")
        SectionTitle("Your living record")
        WarmCard("Tasks", "$completed completed · ${tasks.count { it.completedAt == null }} still open")
        WarmCard("Projects", "${projects.size} active")
        WarmCard("Journey", "${journal.size} diary entries · ${ideas.size} ideas")
        WarmCard("Privacy", "Local-first foundation. Your V1 data stays on this device.")
    }
}
