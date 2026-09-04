package com.navin.personallifeos.ui.screens

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.navin.personallifeos.ui.viewmodel.HomeViewModel
import java.text.DateFormat
import java.util.Date

@Composable
fun JourneyScreen(viewModel: HomeViewModel = hiltViewModel()) {
    val journal by viewModel.journal.collectAsState()
    val ideas by viewModel.ideas.collectAsState()
    val activity by viewModel.activity.collectAsState()

    ScreenColumn {
        Text("JOURNEY", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.tertiary)
        Text("Your life, remembered", style = MaterialTheme.typography.displaySmall)

        SectionTitle("Diary")
        if (journal.isEmpty()) {
            EmptyCard("No diary entries yet", "Capture a thought as Diary. Even one sentence is enough to start your Journey.")
        } else {
            journal.take(3).forEach { entry ->
                WarmCard(entry.title, entry.body.take(180))
            }
        }

        SectionTitle("Ideas")
        if (ideas.isEmpty()) {
            EmptyCard("No ideas captured", "Use phrases like “Idea:” or “What if…” in Capture and they’ll live here.")
        } else {
            ideas.take(3).forEach { idea -> WarmCard(idea.title, idea.body.take(150)) }
        }

        SectionTitle("Recent timeline")
        activity.take(4).forEach { event ->
            WarmCard(
                DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT).format(Date(event.occurredAt)),
                event.title,
            )
        }
    }
}
