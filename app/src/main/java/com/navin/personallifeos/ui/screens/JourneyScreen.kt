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
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
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
import com.navin.personallifeos.data.local.JournalEntryEntity
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
fun JourneyScreen(
    onSearch: () -> Unit = {},
    onWrite: () -> Unit = {},
    onOpenTask: (String) -> Unit = {},
    onOpenJournal: (String) -> Unit = {},
    onOpenIdea: (String) -> Unit = {},
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val journal by viewModel.journal.collectAsState()
    val activity by viewModel.activity.collectAsState()
    val todayStart = dayStart(System.currentTimeMillis())
    var selectedDay by rememberSaveable { mutableLongStateOf(todayStart) }
    val selectedEnd = selectedDay + 86_400_000L
    val entriesForDay = journal.filter { it.createdAt in selectedDay until selectedEnd }
    val eventsForDay = activity.filter { it.occurredAt in selectedDay until selectedEnd }
    val latest = entriesForDay.firstOrNull()

    Column(
        modifier = Modifier.verticalScroll(rememberScrollState()).padding(start = 20.dp, end = 20.dp, top = 20.dp, bottom = 18.dp),
    ) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Column {
                Text("YOUR STORY, QUIETLY KEPT", fontSize = 11.sp, letterSpacing = 1.6.sp, color = Color(0xFF7B796F), fontWeight = FontWeight.Bold)
                Text("Journey", fontSize = 34.sp, lineHeight = 36.sp, fontWeight = FontWeight.Bold, letterSpacing = (-0.7).sp, modifier = Modifier.padding(top = 6.dp))
            }
            Surface(onClick = onSearch, modifier = Modifier.size(42.dp), shape = CircleShape, color = CardCream) {
                Box(contentAlignment = Alignment.Center) { Text("⌕", fontSize = 18.sp, color = Moss) }
            }
        }

        val monday = Calendar.getInstance().apply {
            timeInMillis = todayStart
            val diff = (get(Calendar.DAY_OF_WEEK) + 5) % 7
            add(Calendar.DAY_OF_YEAR, -diff)
        }
        Row(modifier = Modifier.padding(top = 18.dp), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            repeat(7) { i ->
                val day = (monday.clone() as Calendar).apply { add(Calendar.DAY_OF_YEAR, i) }
                val start = dayStart(day.timeInMillis)
                JourneyDayChip(
                    weekday = SimpleDateFormat("EEE", Locale.getDefault()).format(day.time),
                    number = day.get(Calendar.DAY_OF_MONTH).toString(),
                    active = start == selectedDay,
                    onClick = { selectedDay = start },
                    modifier = Modifier.weight(1f),
                )
            }
        }

        JourneySectionHeader(
            if (selectedDay == todayStart) "Today’s page" else SimpleDateFormat("EEEE’s page", Locale.getDefault()).format(Date(selectedDay)),
            if (latest == null) "Write" else "Write more",
            onWrite,
        )
        JournalHero(entry = latest, selectedDay = selectedDay, onWrite = onWrite, onOpen = onOpenJournal)

        JourneySectionHeader("Timeline", if (eventsForDay.isEmpty()) null else "${eventsForDay.size} events", null)
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(22.dp),
            color = Color(0xFFFBF8F1),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE7E1D5)),
        ) {
            if (eventsForDay.isEmpty()) {
                Text("Nothing has been recorded for this day yet.", fontSize = 12.sp, lineHeight = 17.sp, color = Color(0xFF777166), modifier = Modifier.padding(16.dp))
            } else {
                Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp)) {
                    eventsForDay.forEachIndexed { index, event ->
                        TimelineRow(
                            event = event,
                            isLast = index == eventsForDay.lastIndex,
                            onClick = {
                                val id = event.entityId ?: return@TimelineRow
                                when {
                                    event.type.startsWith("task") || event.type.startsWith("reminder") -> onOpenTask(id)
                                    event.type == "journal" -> onOpenJournal(id)
                                    event.type == "idea" -> onOpenIdea(id)
                                }
                            },
                        )
                    }
                }
            }
        }

        JourneySectionHeader("Memories & chapters", "Search all", onSearch)
        val monthStart = Calendar.getInstance().apply {
            set(Calendar.DAY_OF_MONTH, 1)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
        val monthEvents = activity.count { it.occurredAt >= monthStart }
        val monthJournal = journal.count { it.createdAt >= monthStart }
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            MemoryCard(
                Modifier.weight(1f),
                LavenderSoft,
                SimpleDateFormat("MMMM", Locale.getDefault()).format(Date()).uppercase(),
                "$monthEvents recorded moments",
                "$monthJournal journal page${if (monthJournal == 1) "" else "s"}",
            )
            MemoryCard(
                Modifier.weight(1f),
                GoldSoft,
                "LATEST MEMORY",
                journal.firstOrNull()?.title ?: "No memory yet",
                journal.firstOrNull()?.createdAt?.let { relativeDate(it) } ?: "Capture one when it matters",
            )
        }
        Spacer(Modifier.height(8.dp))
    }
}

@Composable
private fun JournalHero(
    entry: JournalEntryEntity?,
    selectedDay: Long,
    onWrite: () -> Unit,
    onOpen: (String) -> Unit,
) {
    Surface(
        onClick = { if (entry != null) onOpen(entry.id) else onWrite() },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        color = CardCream,
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE7E1D5)),
        shadowElevation = 2.dp,
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Text(
                SimpleDateFormat("MMMM d · EEEE", Locale.getDefault()).format(Date(selectedDay)),
                fontSize = 11.sp,
                color = Color(0xFF6C675E),
                modifier = Modifier.background(Color(0xFFF2EEE6), RoundedCornerShape(999.dp)).padding(horizontal = 9.dp, vertical = 6.dp),
            )
            if (entry == null) {
                Text("Nothing written for this day yet.", fontFamily = FontFamily.Serif, fontSize = 22.sp, lineHeight = 27.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(top = 14.dp))
                Text("Tap here to write something worth remembering.", fontSize = 13.sp, lineHeight = 19.sp, color = Color(0xFF6F695F), modifier = Modifier.padding(top = 7.dp))
            } else {
                Text(entry.title, fontFamily = FontFamily.Serif, fontSize = 24.sp, lineHeight = 28.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF2B2A26), modifier = Modifier.padding(top = 14.dp), maxLines = 3)
                Text(entry.body.take(300), fontFamily = FontFamily.Serif, fontSize = 14.sp, lineHeight = 21.sp, color = Color(0xFF5F5A52), modifier = Modifier.padding(top = 8.dp), maxLines = 6)
                Text("Tap to open & edit", fontSize = 10.5.sp, color = Moss, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 12.dp))
            }
        }
    }
}

@Composable
private fun JourneyDayChip(weekday: String, number: String, active: Boolean, onClick: () -> Unit, modifier: Modifier) {
    Surface(
        onClick = onClick,
        modifier = modifier.height(56.dp),
        shape = RoundedCornerShape(16.dp),
        color = if (active) Moss else Color(0xFFFBF8F1),
        border = androidx.compose.foundation.BorderStroke(1.dp, if (active) Moss else Color(0xFFE7E1D5)),
        shadowElevation = if (active) 3.dp else 0.dp,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
            Text(weekday.uppercase(), fontSize = 8.sp, color = if (active) Color(0xFFEAF0E8) else Color(0xFF9A9589))
            Text(number, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = if (active) Color.White else Color(0xFF252521), modifier = Modifier.padding(top = 3.dp))
        }
    }
}

@Composable
private fun JourneySectionHeader(title: String, action: String?, onAction: (() -> Unit)?) {
    Row(modifier = Modifier.fillMaxWidth().padding(top = 20.dp, bottom = 10.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        Text(title, fontSize = 15.sp, fontWeight = FontWeight.Bold)
        if (action != null) {
            if (onAction != null) {
                Surface(onClick = onAction, color = Color.Transparent) { Text(action, fontSize = 11.sp, color = Moss, fontWeight = FontWeight.Bold, modifier = Modifier.padding(5.dp)) }
            } else {
                Text(action, fontSize = 11.sp, color = Color(0xFF8B857B), fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun TimelineRow(event: ActivityEventEntity, isLast: Boolean, onClick: () -> Unit) {
    val clickable = event.entityId != null && (event.type.startsWith("task") || event.type.startsWith("reminder") || event.type == "journal" || event.type == "idea")
    val content: @Composable () -> Unit = {
        Row(modifier = Modifier.fillMaxWidth().height(58.dp)) {
            Text(SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date(event.occurredAt)), fontSize = 9.5.sp, color = Color(0xFF8E897E), modifier = Modifier.padding(top = 2.dp).weight(0.20f))
            Box(modifier = Modifier.weight(0.08f).height(58.dp), contentAlignment = Alignment.TopCenter) {
                if (!isLast) Box(modifier = Modifier.padding(top = 12.dp).size(width = 1.dp, height = 46.dp).background(Color(0xFFDDD6CA)))
                Box(modifier = Modifier.padding(top = 2.dp).size(10.dp).clip(CircleShape).background(eventColor(event.type)))
            }
            Column(modifier = Modifier.weight(0.72f)) {
                Text(event.title, fontSize = 12.sp, fontWeight = FontWeight.Bold, maxLines = 2)
                Text(event.type.replace('_', ' ').replaceFirstChar { it.uppercase() }, fontSize = 10.5.sp, color = Color(0xFF747066), modifier = Modifier.padding(top = 3.dp))
            }
        }
    }
    if (clickable) Surface(onClick = onClick, color = Color.Transparent, content = content) else content()
}

@Composable
private fun MemoryCard(modifier: Modifier, color: Color, kicker: String, main: String, meta: String) {
    Surface(modifier = modifier.height(112.dp), shape = RoundedCornerShape(21.dp), color = color, border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE7E1D5))) {
        Column(modifier = Modifier.padding(15.dp)) {
            Text(kicker, fontSize = 9.5.sp, letterSpacing = 0.8.sp, color = Color(0xFF7A746B), fontWeight = FontWeight.Bold)
            Text(main, fontSize = 14.sp, lineHeight = 17.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 8.dp), maxLines = 2)
            Text(meta, fontSize = 10.sp, color = Color(0xFF7F796E), modifier = Modifier.padding(top = 7.dp), maxLines = 1)
        }
    }
}

private fun eventColor(type: String): Color = when {
    type.contains("task") -> Moss
    type == "journal" -> Color(0xFF9186A5)
    type == "idea" -> Color(0xFFC89B58)
    else -> Color(0xFFA06B59)
}

private fun dayStart(time: Long): Long = Calendar.getInstance().apply {
    timeInMillis = time
    set(Calendar.HOUR_OF_DAY, 0)
    set(Calendar.MINUTE, 0)
    set(Calendar.SECOND, 0)
    set(Calendar.MILLISECOND, 0)
}.timeInMillis

private fun relativeDate(time: Long): String {
    val days = ((dayStart(System.currentTimeMillis()) - dayStart(time)) / 86_400_000L).toInt()
    return when (days) {
        0 -> "Today"
        1 -> "Yesterday"
        else -> SimpleDateFormat("MMM d", Locale.getDefault()).format(Date(time))
    }
}
