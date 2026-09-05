package com.navin.personallifeos.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.navin.personallifeos.data.local.ActivityEventEntity
import com.navin.personallifeos.data.local.IdeaEntity
import com.navin.personallifeos.data.local.JournalEntryEntity
import com.navin.personallifeos.data.local.ProjectEntity
import com.navin.personallifeos.data.local.TaskEntity
import com.navin.personallifeos.data.preferences.AppPreferences
import com.navin.personallifeos.data.repository.LifeRepository
import com.navin.personallifeos.reminders.ReminderScheduler
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: LifeRepository,
    private val reminderScheduler: ReminderScheduler,
    private val preferences: AppPreferences,
) : ViewModel() {
    val pendingTasks: StateFlow<List<TaskEntity>> = repository.pendingTasks()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val allTasks: StateFlow<List<TaskEntity>> = repository.tasks()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val projects: StateFlow<List<ProjectEntity>> = repository.activeProjects()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val journal: StateFlow<List<JournalEntryEntity>> = repository.journal()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val ideas: StateFlow<List<IdeaEntity>> = repository.ideas()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val activity: StateFlow<List<ActivityEventEntity>> = repository.recentActivity(100)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val preferredName: StateFlow<String> = preferences.preferredName
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), "Navin")

    val focusAreas: StateFlow<Set<String>> = preferences.focusAreas
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptySet())

    val lifeAreas: StateFlow<Set<String>> = preferences.lifeAreas
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptySet())

    val morningBrief: StateFlow<Boolean> = preferences.morningBrief
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), true)

    val eveningReflection: StateFlow<Boolean> = preferences.eveningReflection
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), true)

    val themeMode: StateFlow<String> = preferences.themeMode
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), "system")

    fun completeTask(task: TaskEntity) {
        viewModelScope.launch {
            repository.completeTask(task)
            reminderScheduler.cancel(task.id)
        }
    }

    fun reopenTask(task: TaskEntity) {
        viewModelScope.launch {
            repository.reopenTask(task)
            task.reminderAt?.takeIf { it > System.currentTimeMillis() }?.let { reminderScheduler.scheduleExact(task.id, task.title, it) }
        }
    }

    fun toggleTask(task: TaskEntity) {
        if (task.completedAt == null) completeTask(task) else reopenTask(task)
    }

    fun updateTask(task: TaskEntity) {
        viewModelScope.launch {
            repository.updateTask(task)
            reminderScheduler.cancel(task.id)
            task.reminderAt?.takeIf { it > System.currentTimeMillis() }?.let { reminderScheduler.scheduleExact(task.id, task.title, it) }
        }
    }

    fun deleteTask(task: TaskEntity) {
        viewModelScope.launch {
            reminderScheduler.cancel(task.id)
            repository.deleteTask(task.id)
        }
    }

    fun saveProject(project: ProjectEntity) {
        viewModelScope.launch { repository.saveProject(project) }
    }

    fun archiveProject(project: ProjectEntity) {
        viewModelScope.launch { repository.archiveProject(project) }
    }

    fun updateJournal(entry: JournalEntryEntity) {
        viewModelScope.launch { repository.updateJournal(entry) }
    }

    fun deleteJournal(entry: JournalEntryEntity) {
        viewModelScope.launch { repository.deleteJournal(entry.id) }
    }

    fun updateIdea(idea: IdeaEntity) {
        viewModelScope.launch { repository.updateIdea(idea) }
    }

    fun deleteIdea(idea: IdeaEntity) {
        viewModelScope.launch { repository.deleteIdea(idea.id) }
    }

    fun setPreferredName(value: String) {
        viewModelScope.launch { preferences.setPreferredName(value) }
    }

    fun setMorningBrief(value: Boolean) {
        viewModelScope.launch { preferences.setMorningBrief(value) }
    }

    fun setEveningReflection(value: Boolean) {
        viewModelScope.launch { preferences.setEveningReflection(value) }
    }

    fun setThemeMode(value: String) {
        viewModelScope.launch { preferences.setThemeMode(value) }
    }
}
