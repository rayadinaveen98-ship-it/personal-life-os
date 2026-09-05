package com.navin.personallifeos.ui.screens

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.navin.personallifeos.domain.CaptureKind
import com.navin.personallifeos.ui.theme.CardCream
import com.navin.personallifeos.ui.theme.InkMuted
import com.navin.personallifeos.ui.theme.LavenderSoft
import com.navin.personallifeos.ui.theme.Moss
import com.navin.personallifeos.ui.theme.MossSoft
import com.navin.personallifeos.ui.viewmodel.CaptureViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun CaptureScreen(
    onClose: () -> Unit,
    initialText: String = "",
    viewModel: CaptureViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    var text by remember(initialText) { mutableStateOf(initialText) }
    var permissionMessage by remember { mutableStateOf<String?>(null) }
    val suggestion by viewModel.suggestion.collectAsState()
    val resolvedProjectTitle by viewModel.resolvedProjectTitle.collectAsState()
    val saved by viewModel.saved.collectAsState()
    val message by viewModel.message.collectAsState()

    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { granted ->
        if (granted) {
            permissionMessage = null
            viewModel.save()
        } else {
            permissionMessage = "Notifications are required for reminders to reach you. Nothing was saved yet."
        }
    }

    fun saveWithRequiredPermissions() {
        val current = suggestion ?: return
        permissionMessage = null
        if (
            current.kind == CaptureKind.REMINDER &&
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
        ) {
            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        } else {
            viewModel.save()
        }
    }

    fun openExactAlarmSettings() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            runCatching {
                context.startActivity(
                    Intent(
                        Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM,
                        Uri.parse("package:${context.packageName}"),
                    ),
                )
            }
        }
    }

    LaunchedEffect(initialText) {
        if (initialText.isNotBlank() && suggestion == null) viewModel.classify(initialText)
    }

    LaunchedEffect(saved, message) {
        if (saved && message == null) onClose()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(start = 20.dp, end = 20.dp, top = 10.dp, bottom = 24.dp),
    ) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            CaptureTopButton("‹", onClose)
            CaptureTopButton("×", onClose)
        }

        Text(
            "UNIVERSAL CAPTURE",
            fontSize = 12.sp,
            letterSpacing = 1.7.sp,
            color = Color(0xFF7B786F),
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 18.dp),
        )
        Text(
            "Say it naturally.",
            fontSize = 34.sp,
            lineHeight = 36.sp,
            fontWeight = FontWeight.ExtraBold,
            letterSpacing = (-0.7).sp,
            modifier = Modifier.padding(top = 7.dp),
        )
        Text(
            "I’ll pull out the action, date, reminder, project and context. You can correct the type before saving.",
            fontSize = 14.sp,
            lineHeight = 20.sp,
            color = Color(0xFF6F6C64),
            modifier = Modifier.padding(top = 8.dp, bottom = 20.dp),
        )

        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            color = CardCream,
            shadowElevation = 3.dp,
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Text("Capture anything.", fontSize = 18.sp, lineHeight = 25.sp, fontWeight = FontWeight.Bold)
                OutlinedTextField(
                    value = text,
                    onValueChange = {
                        text = it
                        permissionMessage = null
                        if (suggestion != null && !saved) viewModel.editAgain()
                    },
                    enabled = !saved,
                    modifier = Modifier.fillMaxWidth().padding(top = 16.dp).heightIn(min = 138.dp),
                    minLines = 5,
                    shape = RoundedCornerShape(18.dp),
                    placeholder = {
                        Text(
                            "Remind me tomorrow at 10 AM to finish the CINEMA import parser",
                            fontSize = 16.sp,
                            lineHeight = 24.sp,
                        )
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedContainerColor = Color(0xFFF7F3EA),
                        focusedContainerColor = Color(0xFFF7F3EA),
                        disabledContainerColor = Color(0xFFF7F3EA),
                        unfocusedBorderColor = Color.Transparent,
                        focusedBorderColor = Moss,
                        disabledBorderColor = Color.Transparent,
                    ),
                )
                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    VoiceCaptureControl(
                        enabled = !saved,
                        onResult = {
                            text = it
                            permissionMessage = null
                            if (suggestion != null) viewModel.editAgain()
                        },
                    )
                    Text("Local rules · private", fontSize = 11.sp, color = Color(0xFF8C887E))
                }
            }
        }

        val current = suggestion
        if (current != null) {
            Surface(
                modifier = Modifier.fillMaxWidth().padding(top = 18.dp),
                shape = RoundedCornerShape(22.dp),
                color = Color(0xFFEEF0E9),
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text("UNDERSTOOD", fontSize = 12.sp, letterSpacing = 1.4.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF65715F))
                        Text(
                            current.kind.displayName(),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF53644F),
                            modifier = Modifier.background(Color(0xFFDCE4D7), RoundedCornerShape(999.dp)).padding(horizontal = 10.dp, vertical = 7.dp),
                        )
                    }
                    CaptureDetectedRow("✓", current.title, detectedTitleMeta(current.kind))
                    current.reminderAt?.let { CaptureDetectedRow("◷", captureDateTime(it), "Exact reminder") }
                        ?: current.dueAt?.let { CaptureDetectedRow("◷", captureDateTime(it), "Due date · no alarm") }
                    val projectLabel = resolvedProjectTitle ?: current.projectHint
                    if (projectLabel != null) {
                        CaptureDetectedRow("▦", projectLabel, if (resolvedProjectTitle != null) "Linked project" else "Project hint · create the project first to link it")
                    }
                    if (current.priority != 0) {
                        CaptureDetectedRow("!", if (current.priority > 0) "High priority" else "Low priority", "Detected from your wording")
                    }
                    current.durationMinutes?.let { CaptureDetectedRow("↗", durationLabel(it), "Activity duration") }
                }
            }

            if (!saved) {
                Text("Is the type right?", fontSize = 13.sp, fontWeight = FontWeight.ExtraBold, modifier = Modifier.padding(top = 18.dp, bottom = 10.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(7.dp)) {
                    CaptureKindChip("Task", CaptureKind.TASK, current.kind, viewModel::selectKind, Modifier.weight(1f))
                    CaptureKindChip("Reminder", CaptureKind.REMINDER, current.kind, viewModel::selectKind, Modifier.weight(1f))
                    CaptureKindChip("Idea", CaptureKind.IDEA, current.kind, viewModel::selectKind, Modifier.weight(1f))
                }
                Row(horizontalArrangement = Arrangement.spacedBy(7.dp), modifier = Modifier.padding(top = 7.dp)) {
                    CaptureKindChip("Journal", CaptureKind.DIARY, current.kind, viewModel::selectKind, Modifier.weight(1f))
                    CaptureKindChip("Activity", CaptureKind.ACTIVITY, current.kind, viewModel::selectKind, Modifier.weight(1f))
                }

                Button(
                    onClick = ::saveWithRequiredPermissions,
                    modifier = Modifier.fillMaxWidth().padding(top = 20.dp),
                    shape = RoundedCornerShape(18.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Moss),
                ) {
                    Text(saveLabel(current.kind), fontSize = 15.sp, fontWeight = FontWeight.ExtraBold, modifier = Modifier.padding(vertical = 7.dp))
                }
            }

            permissionMessage?.let { CaptureMessage(it) }
            message?.let { CaptureMessage(it) }

            if (saved && current.kind == CaptureKind.REMINDER && message != null) {
                Button(
                    onClick = ::openExactAlarmSettings,
                    modifier = Modifier.fillMaxWidth().padding(top = 10.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Moss),
                ) { Text("Allow exact alarms", fontWeight = FontWeight.Bold, modifier = Modifier.padding(vertical = 5.dp)) }
                Surface(onClick = viewModel::retryExactAlarm, color = Color.Transparent, modifier = Modifier.fillMaxWidth().padding(top = 4.dp)) {
                    Text("I allowed it · Finish reminder setup", color = Moss, fontSize = 12.sp, fontWeight = FontWeight.ExtraBold, modifier = Modifier.padding(10.dp))
                }
            }

            if (!saved) {
                Surface(onClick = viewModel::editAgain, color = Color.Transparent, modifier = Modifier.padding(top = 5.dp)) {
                    Text("Edit & understand again", fontSize = 12.sp, color = Moss, fontWeight = FontWeight.Bold, modifier = Modifier.padding(vertical = 8.dp))
                }
            }
        } else {
            Button(
                onClick = { viewModel.classify(text) },
                enabled = text.isNotBlank(),
                modifier = Modifier.fillMaxWidth().padding(top = 18.dp),
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Moss),
            ) {
                Text("Understand this", fontWeight = FontWeight.Bold, modifier = Modifier.padding(vertical = 6.dp))
            }
        }

        Surface(modifier = Modifier.fillMaxWidth().padding(top = 20.dp), shape = RoundedCornerShape(20.dp), color = Color(0xFFF1ECE3)) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("TRY SAYING", fontSize = 11.sp, letterSpacing = 1.2.sp, color = Color(0xFF8B857B), fontWeight = FontWeight.ExtraBold)
                listOf(
                    "“Tomorrow at 9 AM work on CINEMA.”",
                    "“Finish the parser by Monday.”",
                    "“Worked on Blender for 45 minutes.”",
                    "“Idea: make weekly reviews more visual.”",
                    "“Today felt productive. Save this to my journal.”",
                ).forEachIndexed { index, example ->
                    Text(example, fontSize = 13.sp, lineHeight = 19.sp, color = Color(0xFF625E57), modifier = Modifier.padding(top = if (index == 0) 8.dp else 6.dp))
                }
            }
        }
    }
}

@Composable
private fun CaptureMessage(text: String) {
    Surface(modifier = Modifier.fillMaxWidth().padding(top = 10.dp), shape = RoundedCornerShape(15.dp), color = LavenderSoft) {
        Text(text, fontSize = 11.5.sp, lineHeight = 16.sp, color = InkMuted, modifier = Modifier.padding(12.dp))
    }
}

@Composable
private fun CaptureTopButton(label: String, onClick: () -> Unit) {
    Surface(onClick = onClick, modifier = Modifier.size(38.dp), shape = RoundedCornerShape(13.dp), color = CardCream, shadowElevation = 2.dp) {
        Box(contentAlignment = Alignment.Center) { Text(label, fontSize = 20.sp) }
    }
}

@Composable
private fun CaptureDetectedRow(icon: String, title: String, meta: String) {
    Row(modifier = Modifier.fillMaxWidth().padding(top = 11.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        Box(modifier = Modifier.size(34.dp).clip(RoundedCornerShape(12.dp)).background(CardCream), contentAlignment = Alignment.Center) { Text(icon, fontSize = 16.sp) }
        Column(modifier = Modifier.weight(1f)) {
            Text(title, fontSize = 14.sp, lineHeight = 18.sp, fontWeight = FontWeight.ExtraBold, maxLines = 2)
            Text(meta, fontSize = 12.sp, color = Color(0xFF77736A), modifier = Modifier.padding(top = 3.dp))
        }
    }
}

@Composable
private fun CaptureKindChip(label: String, kind: CaptureKind, selected: CaptureKind, onSelect: (CaptureKind) -> Unit, modifier: Modifier) {
    val active = kind == selected
    Surface(
        onClick = { onSelect(kind) },
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        color = if (active) MossSoft else CardCream,
    ) {
        Box(modifier = Modifier.padding(vertical = 10.dp), contentAlignment = Alignment.Center) {
            Text(label, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = if (active) Moss else Color(0xFF6C675E))
        }
    }
}

private fun CaptureKind.displayName(): String = when (this) {
    CaptureKind.TASK -> "Task"
    CaptureKind.REMINDER -> "Reminder"
    CaptureKind.DIARY -> "Journal"
    CaptureKind.IDEA -> "Idea"
    CaptureKind.ACTIVITY -> "Activity"
}

private fun detectedTitleMeta(kind: CaptureKind): String = when (kind) {
    CaptureKind.TASK -> "Action"
    CaptureKind.REMINDER -> "Reminder action"
    CaptureKind.DIARY -> "Journal memory"
    CaptureKind.IDEA -> "Idea"
    CaptureKind.ACTIVITY -> "Completed activity"
}

private fun saveLabel(kind: CaptureKind): String = when (kind) {
    CaptureKind.TASK -> "Save task"
    CaptureKind.REMINDER -> "Save reminder"
    CaptureKind.DIARY -> "Save to journal"
    CaptureKind.IDEA -> "Save idea"
    CaptureKind.ACTIVITY -> "Log activity"
}

private fun captureDateTime(time: Long): String = SimpleDateFormat("EEE, MMM d · h:mm a", Locale.getDefault()).format(Date(time))

private fun durationLabel(minutes: Int): String = if (minutes >= 60) {
    val hours = minutes / 60
    val rest = minutes % 60
    if (rest == 0) "$hours hr" else "$hours hr $rest min"
} else "$minutes min"

@Composable
private fun VoiceCaptureControl(enabled: Boolean, onResult: (String) -> Unit) {
    val context = LocalContext.current
    var listening by remember { mutableStateOf(false) }
    var status by remember { mutableStateOf<String?>(null) }
    val recognizerAvailable = remember { SpeechRecognizer.isRecognitionAvailable(context) }
    val recognizer = remember(recognizerAvailable) {
        if (recognizerAvailable) SpeechRecognizer.createSpeechRecognizer(context) else null
    }

    fun startListening() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
            putExtra(RecognizerIntent.EXTRA_PREFER_OFFLINE, true)
        }
        status = "Listening…"
        listening = true
        recognizer?.startListening(intent)
    }

    val permissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        if (granted) startListening() else status = "Microphone permission is needed."
    }

    DisposableEffect(recognizer) {
        val listener = object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) { status = "Listening…" }
            override fun onBeginningOfSpeech() { listening = true }
            override fun onRmsChanged(rmsdB: Float) = Unit
            override fun onBufferReceived(buffer: ByteArray?) = Unit
            override fun onEndOfSpeech() { listening = false; status = "Processing…" }
            override fun onError(error: Int) { listening = false; status = "Try again or type instead." }
            override fun onResults(results: Bundle?) {
                listening = false
                val best = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)?.firstOrNull()
                if (!best.isNullOrBlank()) {
                    onResult(best)
                    status = "Voice captured"
                }
            }
            override fun onPartialResults(partialResults: Bundle?) {
                val best = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)?.firstOrNull()
                if (!best.isNullOrBlank()) onResult(best)
            }
            override fun onEvent(eventType: Int, params: Bundle?) = Unit
        }
        recognizer?.setRecognitionListener(listener)
        onDispose {
            recognizer?.cancel()
            recognizer?.destroy()
        }
    }

    Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Surface(
                onClick = {
                    if (!enabled) return@Surface
                    if (!recognizerAvailable) {
                        status = "Speech recognition isn’t available."
                    } else if (ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
                        if (listening) {
                            recognizer?.stopListening()
                            listening = false
                        } else startListening()
                    } else {
                        permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                    }
                },
                modifier = Modifier.size(38.dp),
                shape = CircleShape,
                color = if (enabled) Moss else Color(0xFFAAA59A),
            ) {
                Box(contentAlignment = Alignment.Center) { Text(if (listening) "■" else "●", color = Color.White, fontSize = 14.sp) }
            }
            Text(if (listening) "Stop" else "Tap to speak", fontSize = 13.sp, color = Color(0xFF5F6F5A), fontWeight = FontWeight.Bold, modifier = Modifier.padding(start = 9.dp))
        }
        status?.let { Text(it, fontSize = 10.sp, color = InkMuted, modifier = Modifier.padding(top = 4.dp)) }
    }
}
