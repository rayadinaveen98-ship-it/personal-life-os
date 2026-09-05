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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.navin.personallifeos.ui.theme.CardCream
import com.navin.personallifeos.ui.theme.GoldSoft
import com.navin.personallifeos.ui.theme.LavenderSoft
import com.navin.personallifeos.ui.theme.Moss
import com.navin.personallifeos.ui.theme.MossSoft
import com.navin.personallifeos.ui.theme.MutedGold
import com.navin.personallifeos.ui.theme.SoftLavender
import com.navin.personallifeos.ui.theme.Terracotta
import com.navin.personallifeos.ui.theme.TerracottaSoft
import com.navin.personallifeos.ui.viewmodel.HomeViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun TodayScreen(viewModel: HomeViewModel = hiltViewModel()) {
    val pending by viewModel.pendingTasks.collectAsState()
    val allTasks by viewModel.allTasks.collectAsState()
    val projects by viewModel.projects.collectAsState()

    val now = Calendar.getInstance()
    val greeting = when (now.get(Calendar.HOUR_OF_DAY)) {
        in 5..11 -> "Good morning"
        in 12..16 -> "Good afternoon"
        else -> "Good evening"
    }
    val focus = pending.firstOrNull()
    val focusProject = projects.firstOrNull { it.id == focus?.projectId }
    val nextReminder = pending.firstOrNull { it.reminderAt != null }
    val completedCount = allTasks.count { it.completedAt != null }
    val totalCount = allTasks.size

    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .padding(start = 20.dp, end = 20.dp, top = 14.dp, bottom = 20.dp),
    ) {
        Text("YOUR DAY", fontSize = 12.sp, color = Color(0xFF737267), letterSpacing = 0.6.sp)
        Text(
            "$greeting,\nNavin.",
            fontSize = 30.sp,
            lineHeight = 31.sp,
            fontWeight = FontWeight.ExtraBold,
            letterSpacing = (-0.7).sp,
            modifier = Modifier.padding(top = 5.dp),
        )
        Text(
            SimpleDateFormat("EEEE, MMMM d", Locale.getDefault()).format(Date()),
            fontSize = 13.sp,
            color = Color(0xFF767469),
            modifier = Modifier.padding(top = 7.dp),
        )

        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 23.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text("Today’s focus", fontSize = 14.sp, fontWeight = FontWeight.ExtraBold)
            Text("Change", fontSize = 11.sp, color = Moss, fontWeight = FontWeight.Bold)
        }

        Surface(
            modifier = Modifier.fillMaxWidth().padding(top = 10.dp),
            shape = RoundedCornerShape(24.dp),
            color = Moss,
        ) {
            Box(modifier = Modifier.height(150.dp)) {
                Box(
                    modifier = Modifier
                        .size(126.dp)
                        .offset(x = 255.dp, y = (-26).dp)
                        .border(1.dp, Color.White.copy(alpha = 0.18f), CircleShape),
                )
                Box(
                    modifier = Modifier
                        .size(78.dp)
                        .offset(x = 250.dp, y = 34.dp)
                        .border(1.dp, Color.White.copy(alpha = 0.12f), CircleShape),
                )
                Column(modifier = Modifier.padding(19.dp)) {
                    Text(
                        "PRIORITY · ${focusProject?.title?.uppercase() ?: "TODAY"}",
                        fontSize = 11.sp,
                        letterSpacing = 1.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White.copy(alpha = 0.78f),
                    )
                    Text(
                        focus?.title ?: "Choose one meaningful thing to move forward",
                        fontSize = 24.sp,
                        lineHeight = 26.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = (-0.4).sp,
                        color = Color.White,
                        modifier = Modifier.fillMaxWidth(0.78f).padding(top = 11.dp),
                        maxLines = 2,
                    )
                    Row(
                        modifier = Modifier.padding(top = 14.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            "${pending.size.coerceAtMost(9)} next actions",
                            fontSize = 11.sp,
                            color = Color.White,
                            modifier = Modifier
                                .background(Color.White.copy(alpha = 0.13f), RoundedCornerShape(999.dp))
                                .padding(horizontal = 10.dp, vertical = 7.dp),
                        )
                        Text("Last worked today", fontSize = 11.sp, color = Color.White.copy(alpha = 0.92f))
                    }
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 23.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text("Keep moving", fontSize = 14.sp, fontWeight = FontWeight.ExtraBold)
            Text("See all", fontSize = 11.sp, color = Moss, fontWeight = FontWeight.Bold)
        }

        Column(modifier = Modifier.padding(top = 10.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                TodayMiniCard(
                    modifier = Modifier.weight(1f),
                    icon = "✓",
                    iconBg = MossSoft,
                    iconColor = Moss,
                    label = "Tasks",
                    title = "$completedCount of $totalCount done",
                    meta = "${pending.size} important left",
                )
                TodayMiniCard(
                    modifier = Modifier.weight(1f),
                    icon = "★",
                    iconBg = GoldSoft,
                    iconColor = Color(0xFF9D6C28),
                    label = "Goal",
                    title = "Game development",
                    meta = "68% this month",
                    showProgress = true,
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                TodayMiniCard(
                    modifier = Modifier.weight(1f),
                    icon = "↗",
                    iconBg = LavenderSoft,
                    iconColor = Color(0xFF75698A),
                    label = "Continue learning",
                    title = "Blender lighting",
                    meta = "Lesson 7 · 32 min",
                )
                TodayMiniCard(
                    modifier = Modifier.weight(1f),
                    icon = "◷",
                    iconBg = TerracottaSoft,
                    iconColor = Terracotta,
                    label = "Reminder",
                    title = nextReminder?.title?.take(28) ?: "Movie release check",
                    meta = nextReminder?.reminderAt?.let { reminderLabel(it) } ?: "Tomorrow · 9:00 AM",
                )
            }
        }

        Surface(
            modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
            shape = RoundedCornerShape(22.dp),
            color = LavenderSoft,
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.Top,
            ) {
                Box(
                    modifier = Modifier.size(32.dp).clip(CircleShape).background(SoftLavender),
                    contentAlignment = Alignment.Center,
                ) { Text("☾", color = Color.White, fontSize = 15.sp) }
                Column {
                    Text("What was worth remembering today?", fontSize = 13.sp, lineHeight = 18.sp, fontWeight = FontWeight.Bold)
                    Text("Tap to add tonight’s reflection", fontSize = 10.5.sp, color = Color(0xFF777081), modifier = Modifier.padding(top = 4.dp))
                }
            }
        }
        Spacer(Modifier.height(8.dp))
    }
}

@Composable
private fun TodayMiniCard(
    modifier: Modifier,
    icon: String,
    iconBg: Color,
    iconColor: Color,
    label: String,
    title: String,
    meta: String,
    showProgress: Boolean = false,
) {
    Surface(
        modifier = modifier.height(112.dp),
        shape = RoundedCornerShape(20.dp),
        color = CardCream,
        shadowElevation = 1.dp,
    ) {
        Column(modifier = Modifier.padding(15.dp)) {
            Box(
                modifier = Modifier.size(28.dp).clip(RoundedCornerShape(9.dp)).background(iconBg),
                contentAlignment = Alignment.Center,
            ) { Text(icon, fontSize = 14.sp, color = iconColor) }
            Text(label, fontSize = 11.sp, color = Color(0xFF77756B), modifier = Modifier.padding(top = 8.dp))
            Text(title, fontSize = 15.sp, lineHeight = 17.sp, fontWeight = FontWeight.ExtraBold, maxLines = 2)
            if (showProgress) {
                Box(
                    modifier = Modifier.fillMaxWidth().padding(top = 7.dp).height(6.dp)
                        .clip(RoundedCornerShape(999.dp)).background(Color(0xFFE6E2D9)),
                ) {
                    Box(
                        modifier = Modifier.fillMaxWidth(0.68f).height(6.dp)
                            .background(MutedGold, RoundedCornerShape(999.dp)),
                    )
                }
            }
            Text(meta, fontSize = 10.5.sp, color = Color(0xFF87857B), modifier = Modifier.padding(top = if (showProgress) 4.dp else 6.dp), maxLines = 1)
        }
    }
}

private fun reminderLabel(time: Long): String {
    val now = Calendar.getInstance()
    val target = Calendar.getInstance().apply { timeInMillis = time }
    val tomorrow = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, 1) }
    val prefix = when {
        target.get(Calendar.YEAR) == tomorrow.get(Calendar.YEAR) && target.get(Calendar.DAY_OF_YEAR) == tomorrow.get(Calendar.DAY_OF_YEAR) -> "Tomorrow"
        target.get(Calendar.YEAR) == now.get(Calendar.YEAR) && target.get(Calendar.DAY_OF_YEAR) == now.get(Calendar.DAY_OF_YEAR) -> "Today"
        else -> SimpleDateFormat("MMM d", Locale.getDefault()).format(Date(time))
    }
    return "$prefix · ${SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date(time))}"
}
