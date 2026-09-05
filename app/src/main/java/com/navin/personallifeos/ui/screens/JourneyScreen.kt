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
import com.navin.personallifeos.data.local.ActivityEventEntity
import com.navin.personallifeos.ui.theme.CardCream
import com.navin.personallifeos.ui.theme.GoldSoft
import com.navin.personallifeos.ui.theme.LavenderSoft
import com.navin.personallifeos.ui.theme.Moss
import com.navin.personallifeos.ui.theme.MossSoft
import com.navin.personallifeos.ui.theme.MutedGold
import com.navin.personallifeos.ui.theme.SoftLavender
import com.navin.personallifeos.ui.viewmodel.HomeViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun JourneyScreen(viewModel: HomeViewModel = hiltViewModel()) {
    val journal by viewModel.journal.collectAsState()
    val activity by viewModel.activity.collectAsState()

    val today = Calendar.getInstance()
    val latest = journal.firstOrNull()
    val heroTitle = latest?.title?.takeIf { it.isNotBlank() } ?: "Today felt like things were finally connecting."
    val heroBody = latest?.body?.takeIf { it.isNotBlank() } ?: "I spent time shaping the Personal Life OS and the idea feels much clearer now. The best part was choosing a direction that feels personal, not like another productivity app."

    Column(
        modifier = Modifier.verticalScroll(rememberScrollState()).padding(start = 20.dp, end = 20.dp, top = 20.dp, bottom = 18.dp),
    ) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Column {
                Text("YOUR STORY, QUIETLY KEPT", fontSize = 11.sp, letterSpacing = 1.6.sp, color = Color(0xFF7B796F), fontWeight = FontWeight.Bold)
                Text("Journey", fontSize = 34.sp, lineHeight = 36.sp, fontWeight = FontWeight.Bold, letterSpacing = (-0.7).sp, modifier = Modifier.padding(top = 6.dp))
            }
            Box(
                modifier = Modifier.size(38.dp).clip(CircleShape).background(CardCream).border(1.dp, Color(0xFFE4DED2), CircleShape),
                contentAlignment = Alignment.Center,
            ) { Text("⌕", fontSize = 18.sp, color = Moss) }
        }

        Row(modifier = Modifier.padding(top = 18.dp), horizontalArrangement = Arrangement.spacedBy(9.dp)) {
            val monday = (today.clone() as Calendar).apply {
                val diff = (get(Calendar.DAY_OF_WEEK) + 5) % 7
                add(Calendar.DAY_OF_YEAR, -diff)
            }
            repeat(6) { i ->
                val day = (monday.clone() as Calendar).apply { add(Calendar.DAY_OF_YEAR, i) }
                JourneyDayChip(
                    weekday = SimpleDateFormat("EEE", Locale.getDefault()).format(day.time),
                    number = day.get(Calendar.DAY_OF_MONTH).toString(),
                    active = day.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR),
                    modifier = Modifier.weight(1f),
                )
            }
        }

        JourneySectionHeader("Today’s page", "Write more")
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            color = CardCream,
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE7E1D5)),
            shadowElevation = 2.dp,
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        SimpleDateFormat("MMMM d · EEEE", Locale.getDefault()).format(Date()),
                        fontSize = 11.sp,
                        color = Color(0xFF6C675E),
                        modifier = Modifier.background(Color(0xFFF2EEE6), RoundedCornerShape(999.dp)).padding(horizontal = 9.dp, vertical = 6.dp),
                    )
                    Text(latest?.mood ?: "☁︎", fontSize = 18.sp)
                }
                Text(heroTitle, fontFamily = FontFamily.Serif, fontSize = 24.sp, lineHeight = 28.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF2B2A26), modifier = Modifier.padding(top = 14.dp), maxLines = 3)
                Text(heroBody.take(240), fontFamily = FontFamily.Serif, fontSize = 14.sp, lineHeight = 21.sp, color = Color(0xFF5F5A52), modifier = Modifier.padding(top = 8.dp), maxLines = 5)
                Row(horizontalArrangement = Arrangement.spacedBy(7.dp), modifier = Modifier.padding(top = 13.dp)) {
                    Tag("Personal Life OS", MossSoft, Color(0xFF61705B))
                    Tag("Reflective", LavenderSoft, Color(0xFF6F647A))
                    Tag("Design milestone", GoldSoft, Color(0xFF9A7541))
                }
            }
        }

        JourneySectionHeader("Today’s timeline", "View day")
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(22.dp),
            color = Color(0xFFFBF8F1),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE7E1D5)),
        ) {
            Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp)) {
                val events = if (activity.isEmpty()) listOf<ActivityEventEntity?>(null, null, null) else activity.take(3).map { it as ActivityEventEntity? }
                events.forEachIndexed { index, event ->
                    TimelineRow(
                        event = event,
                        fallbackTitle = listOf("Focused on CINEMA", "Captured an idea", "Design direction locked")[index],
                        fallbackBody = listOf("1h 35m · Database planning and cleanup", "A personal app that remembers how everything connects.", "Warm Personal Observatory chosen for the app.")[index],
                        dotColor = listOf(Moss, SoftLavender, MutedGold)[index],
                        isLast = index == 2,
                    )
                }
            }
        }

        JourneySectionHeader("Memories & chapters", "See all")
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            MemoryCard(Modifier.weight(1.1f), LavenderSoft, "SEPTEMBER CHAPTER", "Building more than consuming.", "7 moments · 3 milestones", "✦")
            MemoryCard(Modifier.weight(0.9f), Color(0xFFF3EADB), "REMEMBER THIS", latest?.title?.take(44) ?: "First real design system locked.", "Today", "◌")
        }
        Spacer(Modifier.height(8.dp))
    }
}

@Composable
private fun JourneyDayChip(weekday: String, number: String, active: Boolean, modifier: Modifier) {
    Surface(
        modifier = modifier.height(56.dp),
        shape = RoundedCornerShape(16.dp),
        color = if (active) Moss else Color(0xFFFBF8F1),
        border = androidx.compose.foundation.BorderStroke(1.dp, if (active) Moss else Color(0xFFE7E1D5)),
        shadowElevation = if (active) 3.dp else 0.dp,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
            Text(weekday.uppercase(), fontSize = 9.sp, color = if (active) Color(0xFFEAF0E8) else Color(0xFF9A9589))
            Text(number, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = if (active) Color.White else Color(0xFF252521), modifier = Modifier.padding(top = 3.dp))
        }
    }
}

@Composable
private fun JourneySectionHeader(title: String, action: String) {
    Row(modifier = Modifier.fillMaxWidth().padding(top = 20.dp, bottom = 10.dp), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(title, fontSize = 15.sp, fontWeight = FontWeight.Bold)
        Text(action, fontSize = 11.sp, color = Moss, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun Tag(text: String, bg: Color, fg: Color) {
    Text(text, fontSize = 10.sp, color = fg, modifier = Modifier.background(bg, RoundedCornerShape(999.dp)).padding(horizontal = 8.dp, vertical = 6.dp))
}

@Composable
private fun TimelineRow(event: ActivityEventEntity?, fallbackTitle: String, fallbackBody: String, dotColor: Color, isLast: Boolean) {
    val time = event?.occurredAt?.let { SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date(it)) } ?: listOf("9:20 AM", "2:10 PM", "7:40 PM").firstOrNull { true } ?: ""
    Row(modifier = Modifier.fillMaxWidth().height(54.dp)) {
        Text(time, fontSize = 9.5.sp, color = Color(0xFF8E897E), modifier = Modifier.padding(top = 2.dp).weight(0.18f))
        Box(modifier = Modifier.weight(0.08f).height(54.dp), contentAlignment = Alignment.TopCenter) {
            if (!isLast) Box(modifier = Modifier.padding(top = 12.dp).size(width = 1.dp, height = 42.dp).background(Color(0xFFDDD6CA)))
            Box(modifier = Modifier.padding(top = 2.dp).size(10.dp).clip(CircleShape).background(dotColor))
        }
        Column(modifier = Modifier.weight(0.74f)) {
            Text(event?.title ?: fallbackTitle, fontSize = 12.sp, fontWeight = FontWeight.Bold, maxLines = 1)
            Text(if (event != null) event.type.replace('_', ' ') else fallbackBody, fontSize = 11.sp, lineHeight = 15.sp, color = Color(0xFF747066), modifier = Modifier.padding(top = 3.dp), maxLines = 2)
        }
    }
}

@Composable
private fun MemoryCard(modifier: Modifier, color: Color, kicker: String, main: String, meta: String, icon: String) {
    Surface(modifier = modifier.height(112.dp), shape = RoundedCornerShape(21.dp), color = color, border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE7E1D5))) {
        Box(modifier = Modifier.padding(15.dp)) {
            Column {
                Text(kicker, fontSize = 9.5.sp, letterSpacing = 0.8.sp, color = Color(0xFF7A746B), fontWeight = FontWeight.Bold)
                Text(main, fontSize = 14.sp, lineHeight = 17.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 8.dp), maxLines = 2)
                Text(meta, fontSize = 10.sp, color = Color(0xFF7F796E), modifier = Modifier.padding(top = 7.dp))
            }
            Text(icon, fontSize = 30.sp, color = Color(0xFF7A746B).copy(alpha = 0.35f), modifier = Modifier.align(Alignment.BottomEnd))
        }
    }
}
