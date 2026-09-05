package com.navin.personallifeos.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.navin.personallifeos.data.local.IdeaEntity
import com.navin.personallifeos.data.local.JournalEntryEntity
import com.navin.personallifeos.data.local.ProjectEntity
import com.navin.personallifeos.ui.theme.CardCream
import com.navin.personallifeos.ui.theme.LavenderSoft
import com.navin.personallifeos.ui.theme.Moss
import com.navin.personallifeos.ui.theme.MossSoft
import com.navin.personallifeos.ui.viewmodel.HomeViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

@Composable
fun TaskDetailScreen(
    taskId: String,
    onBack: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val tasks by viewModel.allTasks.collectAsState()
    val projects by viewModel.projects.collectAsState()
    val task = tasks.firstOrNull { it.id == taskId }
    var title by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    var priority by remember { mutableIntStateOf(0) }

    LaunchedEffect(task?.updatedAt) {
        task?.let {
            title = it.title
            notes = it.notes
            priority = it.priority
        }
    }

    DetailFrame(onBack = onBack, eyebrow = "ACTION", title = "Task") {
        if (task == null) {
            EmptyDetail("This task is no longer available.")
            return@DetailFrame
        }
        val project = projects.firstOrNull { it.id == task.projectId }
        StatusPill(if (task.completedAt == null) "Open" else "Completed")
        Text("Title", fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 18.dp))
        OutlinedTextField(value = title, onValueChange = { title = it }, modifier = Modifier.fillMaxWidth().padding(top = 7.dp), shape = RoundedCornerShape(18.dp))
        Text("Notes", fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 16.dp))
        OutlinedTextField(value = notes, onValueChange = { notes = it }, modifier = Modifier.fillMaxWidth().padding(top = 7.dp), minLines = 4, shape = RoundedCornerShape(18.dp))

        Text("Priority", fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 16.dp, bottom = 8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            PriorityChoice("Low", -1, priority, { priority = it }, Modifier.weight(1f))
            PriorityChoice("Normal", 0, priority, { priority = it }, Modifier.weight(1f))
            PriorityChoice("High", 2, priority, { priority = it }, Modifier.weight(1f))
        }

        DetailInfo("Project", project?.title ?: "No project linked")
        task.dueAt?.let { DetailInfo("Due", formatDateTime(it)) }
        task.reminderAt?.let { DetailInfo("Reminder", formatDateTime(it)) }
        DetailInfo("Created", formatDateTime(task.createdAt))

        Button(
            onClick = {
                if (title.isNotBlank()) {
                    viewModel.updateTask(task.copy(title = title.trim(), notes = notes.trim(), priority = priority))
                    onBack()
                }
            },
            enabled = title.isNotBlank(),
            modifier = Modifier.fillMaxWidth().padding(top = 20.dp),
            shape = RoundedCornerShape(18.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Moss),
        ) { Text("Save changes", fontWeight = FontWeight.ExtraBold, modifier = Modifier.padding(vertical = 5.dp)) }

        Surface(
            onClick = { viewModel.toggleTask(task); onBack() },
            modifier = Modifier.fillMaxWidth().padding(top = 9.dp),
            shape = RoundedCornerShape(18.dp),
            color = MossSoft,
        ) {
            Text(if (task.completedAt == null) "Mark complete" else "Mark open again", color = Moss, fontWeight = FontWeight.ExtraBold, modifier = Modifier.padding(16.dp))
        }
        Surface(
            onClick = { viewModel.deleteTask(task); onBack() },
            modifier = Modifier.fillMaxWidth().padding(top = 9.dp),
            shape = RoundedCornerShape(18.dp),
            color = Color(0xFFF2E4DE),
        ) { Text("Delete task", color = Color(0xFF965D4B), fontWeight = FontWeight.Bold, modifier = Modifier.padding(16.dp)) }
    }
}

@Composable
fun ProjectDetailScreen(
    projectId: String?,
    onBack: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val projects by viewModel.projects.collectAsState()
    val existing = projectId?.let { id -> projects.firstOrNull { it.id == id } }
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var milestone by remember { mutableStateOf("") }

    LaunchedEffect(existing?.updatedAt, projectId) {
        title = existing?.title.orEmpty()
        description = existing?.description.orEmpty()
        milestone = existing?.currentMilestone.orEmpty()
    }

    DetailFrame(onBack = onBack, eyebrow = "PROJECT", title = if (projectId == null) "New project" else "Project") {
        Text("Name", fontSize = 12.sp, fontWeight = FontWeight.Bold)
        OutlinedTextField(value = title, onValueChange = { title = it }, modifier = Modifier.fillMaxWidth().padding(top = 7.dp), shape = RoundedCornerShape(18.dp))
        Text("What are you building?", fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 16.dp))
        OutlinedTextField(value = description, onValueChange = { description = it }, modifier = Modifier.fillMaxWidth().padding(top = 7.dp), minLines = 4, shape = RoundedCornerShape(18.dp))
        Text("Current milestone", fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 16.dp))
        OutlinedTextField(value = milestone, onValueChange = { milestone = it }, modifier = Modifier.fillMaxWidth().padding(top = 7.dp), shape = RoundedCornerShape(18.dp))

        Button(
            onClick = {
                if (title.isBlank()) return@Button
                val now = System.currentTimeMillis()
                viewModel.saveProject(
                    ProjectEntity(
                        id = existing?.id ?: UUID.randomUUID().toString(),
                        title = title.trim(),
                        description = description.trim(),
                        status = "active",
                        currentMilestone = milestone.trim(),
                        createdAt = existing?.createdAt ?: now,
                        updatedAt = now,
                    ),
                )
                onBack()
            },
            enabled = title.isNotBlank(),
            modifier = Modifier.fillMaxWidth().padding(top = 20.dp),
            shape = RoundedCornerShape(18.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Moss),
        ) { Text(if (existing == null) "Create project" else "Save project", fontWeight = FontWeight.ExtraBold, modifier = Modifier.padding(vertical = 5.dp)) }

        if (existing != null) {
            Surface(
                onClick = { viewModel.archiveProject(existing); onBack() },
                modifier = Modifier.fillMaxWidth().padding(top = 9.dp),
                shape = RoundedCornerShape(18.dp),
                color = Color(0xFFF1ECE3),
            ) { Text("Archive project", color = Color(0xFF6F695F), fontWeight = FontWeight.Bold, modifier = Modifier.padding(16.dp)) }
        }
    }
}

@Composable
fun JournalDetailScreen(
    entryId: String,
    onBack: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val journal by viewModel.journal.collectAsState()
    val entry = journal.firstOrNull { it.id == entryId }
    var title by remember { mutableStateOf("") }
    var body by remember { mutableStateOf("") }

    LaunchedEffect(entry?.updatedAt) {
        title = entry?.title.orEmpty()
        body = entry?.body.orEmpty()
    }

    DetailFrame(onBack = onBack, eyebrow = "JOURNEY", title = "Journal") {
        if (entry == null) {
            EmptyDetail("This journal entry is no longer available.")
            return@DetailFrame
        }
        Text(formatDateTime(entry.createdAt), fontSize = 12.sp, color = Color(0xFF7A756B))
        OutlinedTextField(value = title, onValueChange = { title = it }, modifier = Modifier.fillMaxWidth().padding(top = 14.dp), shape = RoundedCornerShape(18.dp))
        OutlinedTextField(value = body, onValueChange = { body = it }, modifier = Modifier.fillMaxWidth().padding(top = 10.dp), minLines = 8, shape = RoundedCornerShape(18.dp))
        Button(
            onClick = { viewModel.updateJournal(entry.copy(title = title.trim(), body = body.trim())); onBack() },
            enabled = title.isNotBlank() && body.isNotBlank(),
            modifier = Modifier.fillMaxWidth().padding(top = 18.dp),
            shape = RoundedCornerShape(18.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Moss),
        ) { Text("Save journal", fontWeight = FontWeight.ExtraBold, modifier = Modifier.padding(vertical = 5.dp)) }
        Surface(onClick = { viewModel.deleteJournal(entry); onBack() }, modifier = Modifier.fillMaxWidth().padding(top = 9.dp), shape = RoundedCornerShape(18.dp), color = Color(0xFFF2E4DE)) {
            Text("Delete entry", color = Color(0xFF965D4B), fontWeight = FontWeight.Bold, modifier = Modifier.padding(16.dp))
        }
    }
}

@Composable
fun IdeaDetailScreen(
    ideaId: String,
    onBack: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val ideas by viewModel.ideas.collectAsState()
    val projects by viewModel.projects.collectAsState()
    val idea = ideas.firstOrNull { it.id == ideaId }
    var title by remember { mutableStateOf("") }
    var body by remember { mutableStateOf("") }

    LaunchedEffect(idea?.updatedAt) {
        title = idea?.title.orEmpty()
        body = idea?.body.orEmpty()
    }

    DetailFrame(onBack = onBack, eyebrow = "IDEA VAULT", title = "Idea") {
        if (idea == null) {
            EmptyDetail("This idea is no longer available.")
            return@DetailFrame
        }
        val project = projects.firstOrNull { it.id == idea.projectId }
        OutlinedTextField(value = title, onValueChange = { title = it }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(18.dp))
        OutlinedTextField(value = body, onValueChange = { body = it }, modifier = Modifier.fillMaxWidth().padding(top = 10.dp), minLines = 7, shape = RoundedCornerShape(18.dp))
        DetailInfo("Project", project?.title ?: "No project linked")
        Button(
            onClick = { viewModel.updateIdea(idea.copy(title = title.trim(), body = body.trim())); onBack() },
            enabled = title.isNotBlank(),
            modifier = Modifier.fillMaxWidth().padding(top = 18.dp),
            shape = RoundedCornerShape(18.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Moss),
        ) { Text("Save idea", fontWeight = FontWeight.ExtraBold, modifier = Modifier.padding(vertical = 5.dp)) }
        Surface(onClick = { viewModel.deleteIdea(idea); onBack() }, modifier = Modifier.fillMaxWidth().padding(top = 9.dp), shape = RoundedCornerShape(18.dp), color = Color(0xFFF2E4DE)) {
            Text("Delete idea", color = Color(0xFF965D4B), fontWeight = FontWeight.Bold, modifier = Modifier.padding(16.dp))
        }
    }
}

@Composable
fun SearchScreen(
    onBack: () -> Unit,
    onTask: (String) -> Unit,
    onProject: (String) -> Unit,
    onJournal: (String) -> Unit,
    onIdea: (String) -> Unit,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val tasks by viewModel.allTasks.collectAsState()
    val projects by viewModel.projects.collectAsState()
    val journal by viewModel.journal.collectAsState()
    val ideas by viewModel.ideas.collectAsState()
    val activity by viewModel.activity.collectAsState()
    var query by remember { mutableStateOf("") }
    val q = query.trim().lowercase()

    DetailFrame(onBack = onBack, eyebrow = "FIND ANYTHING", title = "Search") {
        OutlinedTextField(
            value = query,
            onValueChange = { query = it },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Tasks, projects, journal, ideas…") },
            singleLine = true,
            shape = RoundedCornerShape(18.dp),
        )
        if (q.isBlank()) {
            EmptyDetail("Search your local Personal Life OS data. Nothing leaves the device.")
            return@DetailFrame
        }
        val taskMatches = tasks.filter { it.title.lowercase().contains(q) || it.notes.lowercase().contains(q) }
        val projectMatches = projects.filter { it.title.lowercase().contains(q) || it.description.lowercase().contains(q) || it.currentMilestone.lowercase().contains(q) }
        val journalMatches = journal.filter { it.title.lowercase().contains(q) || it.body.lowercase().contains(q) }
        val ideaMatches = ideas.filter { it.title.lowercase().contains(q) || it.body.lowercase().contains(q) }
        val activityMatches = activity.filter { it.title.lowercase().contains(q) || it.type.lowercase().contains(q) }

        if (taskMatches.isEmpty() && projectMatches.isEmpty() && journalMatches.isEmpty() && ideaMatches.isEmpty() && activityMatches.isEmpty()) {
            EmptyDetail("No matches yet.")
        }
        SearchSection("Tasks", taskMatches.map { SearchItem(it.title, if (it.completedAt == null) "Open task" else "Completed task") { onTask(it.id) } })
        SearchSection("Projects", projectMatches.map { SearchItem(it.title, it.currentMilestone.ifBlank { "Active project" }) { onProject(it.id) } })
        SearchSection("Journal", journalMatches.map { SearchItem(it.title, formatDateTime(it.createdAt)) { onJournal(it.id) } })
        SearchSection("Ideas", ideaMatches.map { SearchItem(it.title, formatDateTime(it.createdAt)) { onIdea(it.id) } })
        SearchSection("Activity", activityMatches.take(10).map { SearchItem(it.title, it.type.replace('_', ' '), null) })
    }
}

@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val preferredName by viewModel.preferredName.collectAsState()
    val morningBrief by viewModel.morningBrief.collectAsState()
    val eveningReflection by viewModel.eveningReflection.collectAsState()
    val themeMode by viewModel.themeMode.collectAsState()
    val lifeAreas by viewModel.lifeAreas.collectAsState()
    var name by remember { mutableStateOf(preferredName) }

    LaunchedEffect(preferredName) { name = preferredName }

    DetailFrame(onBack = onBack, eyebrow = "YOUR SPACE", title = "Settings") {
        Text("Preferred name", fontSize = 12.sp, fontWeight = FontWeight.Bold)
        OutlinedTextField(value = name, onValueChange = { name = it }, modifier = Modifier.fillMaxWidth().padding(top = 7.dp), singleLine = true, shape = RoundedCornerShape(18.dp))
        Surface(onClick = { viewModel.setPreferredName(name) }, modifier = Modifier.fillMaxWidth().padding(top = 8.dp), shape = RoundedCornerShape(16.dp), color = MossSoft) {
            Text("Save name", color = Moss, fontWeight = FontWeight.ExtraBold, modifier = Modifier.padding(14.dp))
        }

        Text("Your rhythm", fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 20.dp, bottom = 8.dp))
        SettingToggle("Morning brief", "Keep the preference ready for the morning briefing feature.", morningBrief, viewModel::setMorningBrief)
        SettingToggle("Evening reflection", "Show the reflection prompt on Today.", eveningReflection, viewModel::setEveningReflection, Modifier.padding(top = 8.dp))

        Text("Appearance", fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 20.dp, bottom = 8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            ThemeChoice("System", "system", themeMode, viewModel::setThemeMode, Modifier.weight(1f))
            ThemeChoice("Light", "light", themeMode, viewModel::setThemeMode, Modifier.weight(1f))
            ThemeChoice("Dark", "dark", themeMode, viewModel::setThemeMode, Modifier.weight(1f))
        }

        Text("Life areas", fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 20.dp, bottom = 8.dp))
        Surface(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(18.dp), color = CardCream) {
            Text(if (lifeAreas.isEmpty()) "No life areas selected" else lifeAreas.sorted().joinToString(" · "), fontSize = 12.sp, lineHeight = 18.sp, color = Color(0xFF6F695F), modifier = Modifier.padding(15.dp))
        }
        Text("Life-area editing will move here after the core interaction pass; current choices remain safely stored.", fontSize = 11.sp, lineHeight = 16.sp, color = Color(0xFF817B71), modifier = Modifier.padding(top = 8.dp))
    }
}

private data class SearchItem(val title: String, val meta: String, val onClick: (() -> Unit)?)

@Composable
private fun SearchSection(title: String, items: List<SearchItem>) {
    if (items.isEmpty()) return
    Text(title.uppercase(), fontSize = 10.5.sp, letterSpacing = 1.1.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF777166), modifier = Modifier.padding(top = 18.dp, bottom = 7.dp))
    Column(verticalArrangement = Arrangement.spacedBy(7.dp)) {
        items.take(12).forEach { item ->
            if (item.onClick != null) {
                Surface(onClick = item.onClick, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(17.dp), color = CardCream) {
                    SearchRow(item)
                }
            } else {
                Surface(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(17.dp), color = CardCream) { SearchRow(item) }
            }
        }
    }
}

@Composable
private fun SearchRow(item: SearchItem) {
    Column(modifier = Modifier.padding(13.dp)) {
        Text(item.title, fontSize = 13.sp, fontWeight = FontWeight.Bold, maxLines = 2)
        Text(item.meta, fontSize = 10.5.sp, color = Color(0xFF7A756B), modifier = Modifier.padding(top = 3.dp))
    }
}

@Composable
private fun DetailFrame(onBack: () -> Unit, eyebrow: String, title: String, content: @Composable () -> Unit) {
    Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 20.dp, vertical = 14.dp)) {
        Surface(onClick = onBack, modifier = Modifier.size(40.dp), shape = CircleShape, color = CardCream) {
            Box(contentAlignment = Alignment.Center) { Text("‹", fontSize = 25.sp) }
        }
        Text(eyebrow, fontSize = 11.sp, letterSpacing = 1.4.sp, color = Moss, fontWeight = FontWeight.ExtraBold, modifier = Modifier.padding(top = 20.dp))
        Text(title, fontSize = 32.sp, lineHeight = 34.sp, fontWeight = FontWeight.ExtraBold, modifier = Modifier.padding(top = 6.dp, bottom = 18.dp))
        content()
        Spacer(Modifier.height(24.dp))
    }
}

@Composable
private fun EmptyDetail(text: String) {
    Surface(modifier = Modifier.fillMaxWidth().padding(top = 12.dp), shape = RoundedCornerShape(20.dp), color = CardCream) {
        Text(text, fontSize = 13.sp, lineHeight = 19.sp, color = Color(0xFF746E65), modifier = Modifier.padding(16.dp))
    }
}

@Composable
private fun StatusPill(text: String) {
    Text(text, fontSize = 11.sp, color = Moss, fontWeight = FontWeight.ExtraBold, modifier = Modifier.background(MossSoft, RoundedCornerShape(999.dp)).padding(horizontal = 10.dp, vertical = 7.dp))
}

@Composable
private fun PriorityChoice(label: String, value: Int, current: Int, onSelect: (Int) -> Unit, modifier: Modifier) {
    Surface(onClick = { onSelect(value) }, modifier = modifier, shape = RoundedCornerShape(14.dp), color = if (value == current) MossSoft else CardCream) {
        Text(label, textAlign = androidx.compose.ui.text.style.TextAlign.Center, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = if (value == current) Moss else Color(0xFF6F695F), modifier = Modifier.padding(vertical = 11.dp))
    }
}

@Composable
private fun DetailInfo(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth().padding(top = 13.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Top) {
        Text(label, fontSize = 11.sp, color = Color(0xFF7A756B))
        Text(value, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(start = 18.dp).weight(1f), textAlign = androidx.compose.ui.text.style.TextAlign.End)
    }
}

@Composable
private fun SettingToggle(title: String, body: String, checked: Boolean, onChange: (Boolean) -> Unit, modifier: Modifier = Modifier) {
    Surface(modifier = modifier.fillMaxWidth(), shape = RoundedCornerShape(18.dp), color = CardCream) {
        Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(title, fontSize = 13.sp, fontWeight = FontWeight.ExtraBold)
                Text(body, fontSize = 10.5.sp, lineHeight = 15.sp, color = Color(0xFF7B756C), modifier = Modifier.padding(top = 3.dp))
            }
            Switch(checked = checked, onCheckedChange = onChange)
        }
    }
}

@Composable
private fun ThemeChoice(label: String, value: String, current: String, onSelect: (String) -> Unit, modifier: Modifier) {
    Surface(onClick = { onSelect(value) }, modifier = modifier, shape = RoundedCornerShape(14.dp), color = if (current == value) MossSoft else CardCream) {
        Text(label, textAlign = androidx.compose.ui.text.style.TextAlign.Center, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = if (current == value) Moss else Color(0xFF6F695F), modifier = Modifier.padding(vertical = 12.dp))
    }
}

private fun formatDateTime(time: Long): String = SimpleDateFormat("EEE, MMM d · h:mm a", Locale.getDefault()).format(Date(time))
