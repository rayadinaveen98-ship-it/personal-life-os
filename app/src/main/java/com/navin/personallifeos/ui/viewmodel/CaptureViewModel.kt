package com.navin.personallifeos.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.navin.personallifeos.data.local.IdeaEntity
import com.navin.personallifeos.data.local.JournalEntryEntity
import com.navin.personallifeos.data.local.TaskEntity
import com.navin.personallifeos.data.repository.LifeRepository
import com.navin.personallifeos.domain.CaptureClassifier
import com.navin.personallifeos.domain.CaptureKind
import com.navin.personallifeos.domain.CaptureSuggestion
import com.navin.personallifeos.reminders.ReminderScheduler
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.LocalDate
import java.util.UUID
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class CaptureViewModel @Inject constructor(
    private val repository: LifeRepository,
    private val reminderScheduler: ReminderScheduler,
) : ViewModel() {
    private val _suggestion = MutableStateFlow<CaptureSuggestion?>(null)
    val suggestion: StateFlow<CaptureSuggestion?> = _suggestion.asStateFlow()

    private val _saved = MutableStateFlow(false)
    val saved: StateFlow<Boolean> = _saved.asStateFlow()

    private val _message = MutableStateFlow<String?>(null)
    val message: StateFlow<String?> = _message.asStateFlow()

    fun classify(text: String) {
        if (text.isBlank()) return
        _suggestion.value = CaptureClassifier.classify(text)
        _message.value = null
    }

    fun selectKind(kind: CaptureKind) {
        val current = _suggestion.value ?: return
        _suggestion.value = current.copy(kind = kind, reminderAt = if (kind == CaptureKind.REMINDER) current.reminderAt else null)
    }

    fun editAgain() {
        _suggestion.value = null
        _message.value = null
    }

    fun save() {
        val suggestion = _suggestion.value ?: return
        viewModelScope.launch {
            val now = System.currentTimeMillis()
            val id = UUID.randomUUID().toString()
            when (suggestion.kind) {
                CaptureKind.TASK, CaptureKind.REMINDER -> {
                    val task = TaskEntity(
                        id = id,
                        title = suggestion.title,
                        notes = suggestion.originalText,
                        dueAt = suggestion.reminderAt,
                        reminderAt = suggestion.reminderAt,
                        priority = if (suggestion.kind == CaptureKind.REMINDER) 1 else 0,
                        createdAt = now,
                        updatedAt = now,
                    )
                    repository.saveTask(task, if (suggestion.kind == CaptureKind.REMINDER) "reminder_created" else "task_created")
                    if (suggestion.kind == CaptureKind.REMINDER && suggestion.reminderAt != null) {
                        val scheduled = reminderScheduler.scheduleExact(id, task.title, suggestion.reminderAt)
                        if (!scheduled) _message.value = "Saved. Exact-alarm permission is still needed for this reminder to fire precisely."
                    }
                }
                CaptureKind.DIARY -> repository.saveJournal(
                    JournalEntryEntity(
                        id = id,
                        title = suggestion.title.ifBlank { "Journal · ${LocalDate.now()}" },
                        body = suggestion.originalText,
                        createdAt = now,
                        updatedAt = now,
                    ),
                )
                CaptureKind.IDEA -> repository.saveIdea(
                    IdeaEntity(
                        id = id,
                        title = suggestion.title,
                        body = suggestion.originalText,
                        createdAt = now,
                        updatedAt = now,
                    ),
                )
            }
            _saved.value = true
        }
    }
}
