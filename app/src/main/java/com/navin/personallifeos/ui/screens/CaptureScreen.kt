package com.navin.personallifeos.ui.screens

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Mic
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.navin.personallifeos.domain.CaptureKind
import com.navin.personallifeos.ui.theme.InkMuted
import com.navin.personallifeos.ui.theme.LavenderSoft
import com.navin.personallifeos.ui.theme.MossSoft
import com.navin.personallifeos.ui.viewmodel.CaptureViewModel
import java.text.DateFormat
import java.util.Date

@Composable
fun CaptureScreen(
    onClose: () -> Unit,
    viewModel: CaptureViewModel = hiltViewModel(),
) {
    var text by remember { mutableStateOf("") }
    val suggestion by viewModel.suggestion.collectAsState()
    val saved by viewModel.saved.collectAsState()
    val message by viewModel.message.collectAsState()

    LaunchedEffect(saved) {
        if (saved && message == null) onClose()
    }

    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                IconButton(onClick = onClose) {
                    Icon(Icons.Outlined.Close, contentDescription = "Close")
                }
                Eyebrow("Universal Capture")
            }

            PageTitle("What’s on your mind?")
            Text(
                "Type it, speak it, or drop the thought here. The app helps organize the rest after you capture it.",
                style = MaterialTheme.typography.bodyLarge,
                color = InkMuted,
            )

            if (suggestion == null) {
                Surface(
                    shape = RoundedCornerShape(24.dp),
                    color = MaterialTheme.colorScheme.surface,
                    shadowElevation = 2.dp,
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        Text("Capture anything", style = MaterialTheme.typography.titleMedium)
                        OutlinedTextField(
                            value = text,
                            onValueChange = { text = it },
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 6,
                            shape = RoundedCornerShape(20.dp),
                            placeholder = { Text("Tomorrow remind me at 10 AM to practice Blender…") },
                        )
                        VoiceCaptureButton(onResult = { spoken -> text = spoken })
                    }
                }

                MetaRow("Task", "Reminder", "Diary", "Idea")

                Button(
                    onClick = { viewModel.classify(text) },
                    enabled = text.isNotBlank(),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                ) { Text("Understand this") }

                AccentCard(
                    eyebrow = "Try something natural",
                    title = "“Worked on Kittu for an hour and finally fixed the build.”",
                    body = "This can become a project update, achievement and Journey event without making you fill a form first.",
                    containerColor = MossSoft,
                )
            } else {
                val current = suggestion ?: return@Column
                AccentCard(
                    eyebrow = "Understood",
                    title = current.kind.name.lowercase().replaceFirstChar { it.uppercase() },
                    body = current.title,
                    containerColor = MossSoft,
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    CaptureKind.entries.forEach { kind ->
                        FilterChip(
                            selected = current.kind == kind,
                            onClick = { viewModel.selectKind(kind) },
                            label = { Text(kind.name.lowercase().replaceFirstChar { it.uppercase() }) },
                        )
                    }
                }

                current.reminderAt?.let {
                    AccentCard(
                        eyebrow = "Reminder time",
                        title = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT).format(Date(it)),
                        body = "Exact user-facing reminder",
                        containerColor = LavenderSoft,
                    )
                }

                message?.let {
                    Text(it, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.secondary)
                }

                Button(
                    onClick = viewModel::save,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                ) { Text("Save to my day") }

                OutlinedButton(
                    onClick = viewModel::editAgain,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                ) { Text("Edit capture") }

                if (saved && message != null) {
                    OutlinedButton(
                        onClick = onClose,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(18.dp),
                    ) { Text("Done") }
                }
            }
        }
    }
}

@Composable
private fun VoiceCaptureButton(onResult: (String) -> Unit) {
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
        if (granted) startListening() else status = "Microphone permission is needed for voice capture."
    }

    DisposableEffect(recognizer) {
        val listener = object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) { status = "Listening…" }
            override fun onBeginningOfSpeech() { listening = true }
            override fun onRmsChanged(rmsdB: Float) = Unit
            override fun onBufferReceived(buffer: ByteArray?) = Unit
            override fun onEndOfSpeech() {
                listening = false
                status = "Processing voice…"
            }
            override fun onError(error: Int) {
                listening = false
                status = "Voice capture didn’t complete. You can try again or type instead."
            }
            override fun onResults(results: Bundle?) {
                listening = false
                val best = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)?.firstOrNull()
                if (!best.isNullOrBlank()) {
                    onResult(best)
                    status = "Voice captured"
                } else {
                    status = "I couldn’t hear enough to capture that."
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

    OutlinedButton(
        onClick = {
            if (!recognizerAvailable) {
                status = "Speech recognition isn’t available on this device."
            } else if (
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.RECORD_AUDIO,
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                if (listening) {
                    recognizer?.stopListening()
                    listening = false
                } else {
                    startListening()
                }
            } else {
                permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
            }
        },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
    ) {
        Icon(Icons.Outlined.Mic, contentDescription = null)
        Text(if (listening) " Stop listening" else " Speak instead")
    }

    status?.let {
        Text(it, style = MaterialTheme.typography.labelSmall, color = InkMuted)
    }
}
