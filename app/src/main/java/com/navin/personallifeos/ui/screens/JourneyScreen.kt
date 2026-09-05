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
import com.navin.personallifeos.ui.viewmodel.HomeViewModel
import java.text.DateFormat
import java.util.Date

@Composable
fun JourneyScreen(viewModel: HomeViewModel = hiltViewModel()) {
    val journal by viewModel.journal.collectAsState()
    val ideas by viewModel.ideas.collectAsState()
    val activity by viewModel.activity.collectAsState()

    ScreenColumn {
        Eyebrow("Your story, quietly kept", MaterialTheme.colorScheme.tertiary)
        PageTitle("Journey")
        Text(
            DateFormat.getDateInstance(DateFormat.LONG).format(Date()),
            style = MaterialTheme.typography.bodyMedium,
            color = InkMuted,
        )

        val latestEntry = journal.firstOrNull()
        AccentCard(
            eyebrow = "Today’s page",
            title = latestEntry?.title ?: "A blank page is still part of the story",
            body = latestEntry?.body?.take(220)
                ?: "Write one thought about today. It does not need to be productive, polished, or long.",
            containerColor = LavenderSoft,
        )

        SectionTitle("Today’s timeline")
        if (activity.isEmpty()) {
            EmptyCard("Nothing recorded yet", "Sessions, completed tasks and milestones from today will appear here.")
        } else {
            activity.take(4).forEach { event ->
                WarmCard(
                    DateFormat.getTimeInstance(DateFormat.SHORT).format(Date(event.occurredAt)),
                    event.title,
                )
            }
        }

        SectionTitle("Memories & ideas")
        if (ideas.isEmpty()) {
            AccentCard(
                eyebrow = "Remember this",
                title = "Your first meaningful moments will surface here",
                body = "Ideas, milestones and reflections can become memories instead of disappearing into separate apps.",
                containerColor = GoldSoft,
            )
        } else {
            ideas.take(2).forEach { idea -> WarmCard(idea.title, idea.body.take(160)) }
        }

        AccentCard(
            eyebrow = "September chapter",
            title = "Building more than consuming",
            body = "A quiet monthly chapter that becomes clearer as your projects, learning and reflections accumulate.",
            containerColor = GoldSoft,
        )
    }
}
