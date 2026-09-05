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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.navin.personallifeos.data.local.TaskEntity
import com.navin.personallifeos.ui.theme.CardCream
import com.navin.personallifeos.ui.theme.GoldSoft
import com.navin.personallifeos.ui.theme.LavenderSoft
import com.navin.personallifeos.ui.theme.Moss
import com.navin.personallifeos.ui.theme.MossSoft
import com.navin.personallifeos.ui.viewmodel.HomeViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun PlanScreen(viewModel: HomeViewModel = hiltViewModel()) {
    val tasks by viewModel.pendingTasks.collectAsState()
    val allTasks by viewModel.allTasks.collectAsState()
    val projects by viewModel.projects.collectAsState()
    val completed = allTasks.count { it.completedAt != null }
    val focus = tasks.firstOrNull()
    val focusProject = projects.firstOrNull { it.id == focus?.projectId }

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
                Text("N", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 20.dp).background(Color(0xFFE8E3D8), RoundedCornerShape(18.dp)).padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            PlanSegment("Today", true, Modifier.weight(1f))
            PlanSegment("Week", false, Modifier.weight(1f))
            PlanSegment("Projects", false, Modifier.weight(1f))
        }

        Surface(
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
                Text(focus?.title ?: focusProject?.title ?: "Choose one meaningful push", fontSize = 20.sp, lineHeight = 24.sp, fontWeight = FontWeight.Bold, color = Color.White, modifier = Modifier.padding(top = 12.dp))
                Text("Keep today light: one meaningful push before switching projects.", fontSize = 13.sp, lineHeight = 18.sp, color = Color(0xFFEEF2EB), modifier = Modifier.padding(top = 8.dp))
                Box(modifier = Modifier.fillMaxWidth().padding(top = 14.dp).height(7.dp).clip(RoundedCornerShape(999.dp)).background(Color.White.copy(alpha = 0.18f))) {
                    Box(modifier = Modifier.fillMaxWidth(if (allTasks.isEmpty()) 0.18f else (completed.toFloat() / allTasks.size.toFloat()).coerceIn(0.08f, 1f)).height(7.dp).background(Color(0xFFF3EEE5), RoundedCornerShape(999.dp)))
                }
                Row(modifier = Modifier.fillMaxWidth().padding(top = 7.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(if (allTasks.isEmpty()) "Ready to begin" else "${(completed * 100 / allTasks.size)}% complete", fontSize = 11.sp, color = Color(0xFFE5EADD))
                    Text("Next: ${focus?.title?.take(18) ?: "capture one action"}", fontSize = 11.sp, color = Color(0xFFE5EADD))
                }
            }
        }

        Row(modifier = Modifier.fillMaxWidth().padding(start = 2.dp, end = 2.dp, top = 19.dp, bottom = 10.dp), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Today", fontSize = 16.sp, fontWeight = FontWeight.ExtraBold)
            Text("+ Add task", fontSize = 11.sp, color = Moss, fontWeight = FontWeight.Bold)
        }
        Column(verticalArrangement = Arrangement.spacedBy(9.dp)) {
            if (tasks.isEmpty()) {
                PlanEmptyCompact("Nothing planned yet", "Use Capture to add your first task or reminder.")
            } else {
                tasks.take(3).forEach { task -> PlanTaskRow(task, onComplete = { viewModel.completeTask(task) }) }
            }
        }

        Row(modifier = Modifier.fillMaxWidth().padding(start = 2.dp, end = 2.dp, top = 19.dp, bottom = 10.dp), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Coming up", fontSize = 16.sp, fontWeight = FontWeight.ExtraBold)
            Text("View all", fontSize = 11.sp, color = Moss, fontWeight = FontWeight.Bold)
        }
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            ComingUpCard(Modifier.weight(1f), LavenderSoft, "⏰", "Movie release check", "Verify tomorrow’s Telugu releases before morning reminder.")
            ComingUpCard(Modifier.weight(1f), GoldSoft, "✨", "Weekly review", "Sunday · see what moved forward and what needs attention.")
        }

        Row(modifier = Modifier.fillMaxWidth().padding(start = 2.dp, end = 2.dp, top = 19.dp, bottom = 10.dp), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Habits", fontSize = 16.sp, fontWeight = FontWeight.ExtraBold)
            Text("Manage", fontSize = 11.sp, color = Moss, fontWeight = FontWeight.Bold)
        }
        Surface(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(18.dp), color = CardCream) {
            Row(modifier = Modifier.padding(horizontal = 13.dp, vertical = 12.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Box(modifier = Modifier.size(32.dp).clip(RoundedCornerShape(12.dp)).background(MossSoft), contentAlignment = Alignment.Center) { Text("📚", fontSize = 15.sp) }
                Column(modifier = Modifier.weight(1f)) {
                    Text("Learn something", fontSize = 12.5.sp, fontWeight = FontWeight.Bold)
                    Row(horizontalArrangement = Arrangement.spacedBy(5.dp), modifier = Modifier.padding(top = 6.dp)) {
                        repeat(5) { i -> Box(modifier = Modifier.size(width = 17.dp, height = 7.dp).background(if (i < 3) Moss else Color(0xFFD8D3C8), RoundedCornerShape(999.dp))) }
                    }
                }
                Text("3/5", fontSize = 11.sp, color = Color(0xFF6F6B62), fontWeight = FontWeight.Bold)
            }
        }
        Spacer(Modifier.height(8.dp))
    }
}

@Composable
private fun PlanSegment(label: String, active: Boolean, modifier: Modifier) {
    Surface(modifier = modifier, shape = RoundedCornerShape(14.dp), color = if (active) CardCream else Color.Transparent, shadowElevation = if (active) 1.dp else 0.dp) {
        Box(modifier = Modifier.padding(vertical = 10.dp), contentAlignment = Alignment.Center) {
            Text(label, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = if (active) Color(0xFF2B2B27) else Color(0xFF777267))
        }
    }
}

@Composable
private fun PlanTaskRow(task: TaskEntity, onComplete: () -> Unit) {
    Surface(onClick = onComplete, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(18.dp), color = CardCream, shadowElevation = 1.dp) {
        Row(modifier = Modifier.padding(13.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Box(modifier = Modifier.size(22.dp).border(2.dp, Color(0xFFA9A496), CircleShape), contentAlignment = Alignment.Center)
            Column(modifier = Modifier.weight(1f)) {
                Text(task.title, fontSize = 13.sp, lineHeight = 16.sp, fontWeight = FontWeight.Bold, maxLines = 1)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(top = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text(if (task.projectId != null) "PROJECT" else "TODAY", fontSize = 9.5.sp, fontWeight = FontWeight.Bold, color = Moss, modifier = Modifier.background(MossSoft, RoundedCornerShape(999.dp)).padding(horizontal = 7.dp, vertical = 3.dp))
                    Text(if (task.reminderAt != null) "Reminder" else "Deep work", fontSize = 10.5.sp, color = Color(0xFF878174))
                }
            }
            Text(task.reminderAt?.let { SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(it)) } ?: "—", fontSize = 11.sp, color = Color(0xFF797469), fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun ComingUpCard(modifier: Modifier, color: Color, icon: String, title: String, body: String) {
    Surface(modifier = modifier.height(104.dp), shape = RoundedCornerShape(20.dp), color = color) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(icon, fontSize = 18.sp)
            Text(title, fontSize = 13.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 8.dp))
            Text(body, fontSize = 10.5.sp, lineHeight = 14.sp, color = Color(0xFF5F5B54), modifier = Modifier.padding(top = 4.dp), maxLines = 3)
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
