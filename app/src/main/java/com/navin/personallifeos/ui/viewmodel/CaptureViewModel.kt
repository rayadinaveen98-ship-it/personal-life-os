package com.navin.personallifeos.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.navin.personallifeos.data.local.IdeaEntity
import com.navin.personallifeos.data.local.JournalEntryEntity
import com.navin.personallifeos.data.local.ProjectEntity
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
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class CaptureViewModel @Inject constructor(
    private val repository: LifeRepository,
    private val reminderScheduler: ReminderScheduler,
) : ViewModel() {
    private val activeProjects: StateFlow<List<ProjectEntity>> = repository.activeProjects()
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    private val _suggestion = MutableStateFlow<CaptureSuggestion?>(null)
    val suggestion: StateFlow<CaptureSuggestion?> = _suggestion.asStateFlow()

    private val _resolvedProjectTitle = MutableStateFlow<String?>(null)
    val resolvedProjectTitle: StateFlow<String?> = _resolvedProjectTitle.asStateFlow()

    private val _saved = MutableStateFlow(false)
    val saved: StateFlow<Boolean> = _saved.asStateFlow()

    private val _message = MutableStateFlow<String?>(null)
    val message: StateFlow<String?> = _message.asStateFlow()

    private var savedEntityId: String? = null

    init {
        viewModelScope.launch {
            activeProjects.collect {
                _suggestion.value?.let { suggestion ->
                    _resolvedProjectTitle.value = resolveProject(suggestion)?.title
                }
            }
        }
    }

    fun classify(text: String) {
        if (text.isBlank()) return
        val classified = CaptureClassifier.classify(text)
        _suggestion.value = classified
        _resolvedProjectTitle.value = resolveProject(classified)?.title
        _saved.value = false
        savedEntityId = null
        _message.value = null
    }

    fun selectKind(kind: CaptureKind) {
        if (_saved.value) return
        val current = _suggestion.value ?: return
        val reminderAt = if (kind == CaptureKind.REMINDER) current.reminderAt ?: current.dueAt else null
        val dueAt = when (kind) {
            CaptureKind.REMINDER -> reminderAt
            CaptureKind.TASK -> current.dueAt ?: current.reminderAt
            else -> null
        }
        _suggestion.value = current.copy(kind = kind, dueAt = dueAt, reminderAt = reminderAt)
        _message.value = null
    }

    fun editAgain() {
        _suggestion.value = null
        _resolvedProjectTitle.value = null
        _saved.value = false
        savedEntityId = null
        _message.value = null
    }

    fun canScheduleExactAlarm(): Boolean = reminderScheduler.canScheduleExact()

    fun save() {
        if (_saved.value) return
        val suggestion = _suggestion.value ?: return
        viewModelScope.launch {
            if (suggestion.kind == CaptureKind.REMINDER && suggestion.reminderAt == null) {
                _message.value = "I understood this as a reminder, but I still need a future date or time. Edit the capture and add one."
                return@launch
            }

            val now = System.currentTimeMillis()
            val id = UUID.randomUUID().toString()
            val project = resolveProject(suggestion)

            when (suggestion.kind) {
                CaptureKind.TASK, CaptureKind.REMINDER -> {
                    val task = TaskEntity(
                        id = id,
                        title = suggestion.title,
                        notes = suggestion.originalText,
                        projectId = project?.id,
                        dueAt = suggestion.dueAt,
                        reminderAt = suggestion.reminderAt,
                        priority = suggestion.priority,
                        createdAt = now,
                        updatedAt = now,
                    )
                    repository.saveTask(
                        task,
                        if (suggestion.kind == CaptureKind.REMINDER) "reminder_created" else "task_created",
                    )
                    savedEntityId = id
                    if (suggestion.kind == CaptureKind.REMINDER && suggestion.reminderAt != null) {
                        val scheduled = reminderScheduler.scheduleExact(id, task.title, suggestion.reminderAt)
                        if (!scheduled) {
                            _message.value = "Saved safely. Allow exact alarms, then tap Finish reminder setup so it can fire precisely."
                        }
                    }
                }

                CaptureKind.DIARY -> {
                    repository.saveJournal(
                        JournalEntryEntity(
                            id = id,
                            title = suggestion.title.ifBlank { "Journal · ${LocalDate.now()}" },
                            body = suggestion.originalText,
                            createdAt = now,
                            updatedAt = now,
                        ),
                    )
                    savedEntityId = id
                }

                CaptureKind.IDEA -> {
                    repository.saveIdea(
                        IdeaEntity(
                            id = id,
                            title = suggestion.title,
                            body = suggestion.originalText,
                            projectId = project?.id,
                            createdAt = now,
                            updatedAt = now,
                        ),
                    )
                    savedEntityId = id
                }

                CaptureKind.ACTIVITY -> {
                    repository.saveActivity(
                        title = suggestion.title,
                        projectId = project?.id,
                        durationMinutes = suggestion.durationMinutes,
                        occurredAt = now,
                    )
                    savedEntityId = id
                }
            }
            _saved.value = true
        }
    }

    fun retryExactAlarm() {
        val id = savedEntityId ?: return
        val suggestion = _suggestion.value ?: return
        val trigger = suggestion.reminderAt ?: return
        if (suggestion.kind != CaptureKind.REMINDER) return
        if (reminderScheduler.scheduleExact(id, suggestion.title, trigger)) {
            _message.value = null
        } else {
            _message.value = "Exact-alarm access is still off. Allow it in Android settings, then try again."
        }
    }

    private fun resolveProject(suggestion: CaptureSuggestion): ProjectEntity? {
        val projects = activeProjects.value
        if (projects.isEmpty()) return null

        val original = normalize(suggestion.originalText)
        projects.sortedByDescending { it.title.length }
            .firstOrNull { project ->
                val title = normalize(project.title)
                title.isNotBlank() && original.contains(title)
            }
            ?.let { return it }

        val hint = suggestion.projectHint?.let(::normalize)?.takeIf(String::isNotBlank) ?: return null
        return projects.maxByOrNull { project ->
            val title = normalize(project.title)
            when {
                title == hint -> 100
                title.contains(hint) || hint.contains(title) -> 80
                title.split(' ').any { it.length > 2 && hint.contains(it) } -> 40
                else -> 0
            }
        }?.takeIf { project ->
            val title = normalize(project.title)
            title == hint || title.contains(hint) || hint.contains(title) ||
                title.split(' ').any { it.length > 2 && hint.contains(it) }
        }
    }

    private fun normalize(value: String): String = value
        .lowercase()
        .replace(Regex("[^a-z0-9]+"), " ")
        .trim()
}
