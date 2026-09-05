package com.navin.personallifeos.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
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
fun MeScreen(
    onOpenPlan: () -> Unit = {},
    onSearch: () -> Unit = {},
    onSettings: () -> Unit = {},
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val tasks by viewModel.allTasks.collectAsState()
    val projects by viewModel.projects.collectAsState()
    val journal by viewModel.journal.collectAsState()
    val ideas by viewModel.ideas.collectAsState()
    val activity by viewModel.activity.collectAsState()
    val preferredName by viewModel.preferredName.collectAsState()
    val focusAreas by viewModel.focusAreas.collectAsState()
    val lifeAreas by viewModel.lifeAreas.collectAsState()

    val name = preferredName.trim().ifBlank { "Navin" }
    val monthStart = Calendar.getInstance().apply {
        set(Calendar.DAY_OF_MONTH, 1)
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }.timeInMillis
    val completedThisMonth = tasks.count { (it.completedAt ?: Long.MIN_VALUE) >= monthStart }
    val journalThisMonth = journal.count { it.createdAt >= monthStart }
    val ideasThisMonth = ideas.count { it.createdAt >= monthStart }
    val activityThisMonth = activity.count { it.occurredAt >= monthStart }
    val latestActivity = activity.firstOrNull()

    Column(modifier = Modifier.verticalScroll(rememberScrollState()).padding(start = 18.dp, end = 18.dp, top = 20.dp, bottom = 20.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text("YOUR PERSONAL SPACE", fontSize = 12.sp, letterSpacing = 1.5.sp, color = Color(0xFF7A756B), fontWeight = FontWeight.Bold)
                Text("Me", fontFamily = FontFamily.Serif, fontSize = 34.sp, lineHeight = 35.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(top = 6.dp))
            }
            Surface(onClick = onSettings, shape = RoundedCornerShape(14.dp), color = CardCream) {
                Text("Settings", fontSize = 11.sp, color = Moss, fontWeight = FontWeight.ExtraBold, modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp))
            }
        }
        Text("A living picture made only from what you actually record.", fontSize = 14.sp, color = Color(0xFF7A756B), modifier = Modifier.padding(top = 2.dp))

        Surface(modifier = Modifier.fillMaxWidth().padding(top = 18.dp), shape = RoundedCornerShape(26.dp), color = Moss, shadowElevation = 4.dp) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Box(modifier = Modifier.size(56.dp).clip(RoundedCornerShape(18.dp)).background(Color(0xFFEDF1E9)), contentAlignment = Alignment.Center) {
                        Text(name.first().uppercaseChar().toString(), color = Moss, fontSize = 21.sp, fontWeight = FontWeight.ExtraBold)
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(name, fontSize = 23.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        Text(
                            focusAreas.sorted().take(3).joinToString(" · ").ifBlank { "Your private life space" },
                            fontSize = 12.sp,
                            lineHeight = 17.sp,
                            color = Color.White.copy(alpha = 0.80f),
                            modifier = Modifier.padding(top = 3.dp),
                            maxLines = 2,
                        )
                    }
                }
                Surface(modifier = Modifier.fillMaxWidth().padding(top = 16.dp), shape = RoundedCornerShape(18.dp), color = Color.White.copy(alpha = 0.12f)) {
                    Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 13.dp)) {
                        Text("RECENT THREAD", fontSize = 10.sp, letterSpacing = 1.2.sp, color = Color.White.copy(alpha = 0.72f), fontWeight = FontWeight.Bold)
                        Text(
                            latestActivity?.title ?: "Nothing recorded yet — your story can start with one small capture.",
                            fontFamily = FontFamily.Serif,
                            fontSize = 18.sp,
                            lineHeight = 23.sp,
                            color = Color.White,
                            modifier = Modifier.padding(top = 5.dp),
                            maxLines = 3,
                        )
                        latestActivity?.let {
                            Text(relativeWhen(it.occurredAt), fontSize = 10.5.sp, color = Color.White.copy(alpha = 0.72f), modifier = Modifier.padding(top = 6.dp))
                        }
                    }
                }
            }
        }

        Row(modifier = Modifier.padding(top = 12.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            MetricCard(Modifier.weight(1f), "Active projects", projects.size.toString(), "Open your real project list", onOpenPlan)
            MetricCard(Modifier.weight(1f), "Life areas", lifeAreas.size.toString(), if (lifeAreas.isEmpty()) "Choose them in setup" else "Stored from your setup", onSettings)
        }

        MeSectionTitle("Current projects", if (projects.isEmpty()) null else "Open plan", onOpenPlan)
        if (projects.isEmpty()) {
            Surface(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(20.dp), color = CardCream) {
                Text("No projects yet. Create the first one from Plan → Projects.", fontSize = 12.sp, lineHeight = 18.sp, color = Color(0xFF746F66), modifier = Modifier.padding(15.dp))
            }
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                projects.take(3).forEach { project ->
                    Surface(onClick = onOpenPlan, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(20.dp), color = CardCream, shadowElevation = 1.dp) {
                        Column(modifier = Modifier.padding(15.dp)) {
                            Text(project.title, fontSize = 14.sp, fontWeight = FontWeight.ExtraBold)
                            Text(project.currentMilestone.ifBlank { project.description.ifBlank { "Active project" } }, fontSize = 11.sp, lineHeight = 16.sp, color = Color(0xFF777166), modifier = Modifier.padding(top = 4.dp), maxLines = 2)
                        }
                    }
                }
            }
        }

        MeSectionTitle("What matters in my life", null, null)
        if (lifeAreas.isEmpty()) {
            Text("No life areas selected yet.", fontSize = 12.sp, color = Color(0xFF777166))
        } else {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                lifeAreas.sorted().take(3).forEachIndexed { index, area ->
                    val bg = when (index % 3) {
                        0 -> MossSoft
                        1 -> LavenderSoft
                        else -> GoldSoft
                    }
                    GrowthChip(area, bg, Color(0xFF5F665A), Modifier.weight(1f))
                }
            }
            if (lifeAreas.size > 3) Text("+${lifeAreas.size - 3} more in Settings", fontSize = 10.5.sp, color = Moss, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 8.dp))
        }

        MeSectionTitle("This month", "Search all", onSearch)
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            MetricCard(Modifier.weight(1f), "Completed actions", completedThisMonth.toString(), "$activityThisMonth recorded events", onSearch)
            MetricCard(Modifier.weight(1f), "Ideas captured", ideasThisMonth.toString(), "$journalThisMonth journal pages", onSearch)
        }

        if (latestActivity != null) {
            Surface(onClick = onSearch, modifier = Modifier.fillMaxWidth().padding(top = 12.dp), shape = RoundedCornerShape(22.dp), color = GoldSoft) {
                Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(11.dp)) {
                    Box(modifier = Modifier.size(42.dp).clip(RoundedCornerShape(15.dp)).background(Color(0xFFD9B56E)), contentAlignment = Alignment.Center) { Text("↗", color = Color.White, fontWeight = FontWeight.ExtraBold) }
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Latest recorded progress", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        Text(latestActivity.title, fontSize = 11.sp, color = Color(0xFF6D6048), modifier = Modifier.padding(top = 3.dp), maxLines = 2)
                    }
                }
            }
        }
        Spacer(Modifier.height(8.dp))
    }
}

@Composable
private fun MetricCard(modifier: Modifier, title: String, metric: String, meta: String, onClick: (() -> Unit)?) {
    val content: @Composable () -> Unit = {
        Column(modifier = Modifier.padding(15.dp)) {
            Text(title, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            Text(metric, fontSize = 27.sp, fontWeight = FontWeight.ExtraBold, modifier = Modifier.padding(top = 5.dp))
            Text(meta, fontSize = 10.5.sp, lineHeight = 15.sp, color = Color(0xFF777166), modifier = Modifier.padding(top = 2.dp), maxLines = 2)
        }
    }
    if (onClick != null) Surface(onClick = onClick, modifier = modifier, shape = RoundedCornerShape(22.dp), color = CardCream, shadowElevation = 1.dp, content = content)
    else Surface(modifier = modifier, shape = RoundedCornerShape(22.dp), color = CardCream, shadowElevation = 1.dp, content = content)
}

@Composable
private fun MeSectionTitle(text: String, action: String?, onAction: (() -> Unit)?) {
    Row(modifier = Modifier.fillMaxWidth().padding(start = 2.dp, top = 18.dp, bottom = 9.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        Text(text.uppercase(), fontSize = 11.sp, letterSpacing = 1.2.sp, color = Color(0xFF777166), fontWeight = FontWeight.ExtraBold)
        if (action != null && onAction != null) {
            Surface(onClick = onAction, color = Color.Transparent) { Text(action, fontSize = 10.5.sp, color = Moss, fontWeight = FontWeight.Bold, modifier = Modifier.padding(4.dp)) }
        }
    }
}

@Composable
private fun GrowthChip(text: String, bg: Color, fg: Color, modifier: Modifier) {
    Box(modifier = modifier.background(bg, RoundedCornerShape(18.dp)).padding(horizontal = 10.dp, vertical = 10.dp), contentAlignment = Alignment.Center) {
        Text(text, fontSize = 11.sp, lineHeight = 14.sp, color = fg, fontWeight = FontWeight.Bold, maxLines = 2)
    }
}

private fun relativeWhen(time: Long): String {
    val diff = (System.currentTimeMillis() - time).coerceAtLeast(0L)
    val minutes = diff / 60_000L
    return when {
        minutes < 1 -> "Just now"
        minutes < 60 -> "${minutes}m ago"
        minutes < 1_440 -> "${minutes / 60}h ago"
        else -> SimpleDateFormat("MMM d", Locale.getDefault()).format(Date(time))
    }
}
