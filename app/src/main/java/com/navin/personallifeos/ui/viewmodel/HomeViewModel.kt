package com.navin.personallifeos.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.navin.personallifeos.data.local.ActivityEventEntity
import com.navin.personallifeos.data.local.IdeaEntity
import com.navin.personallifeos.data.local.JournalEntryEntity
import com.navin.personallifeos.data.local.ProjectEntity
import com.navin.personallifeos.data.local.TaskEntity
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

    val activity: StateFlow<List<ActivityEventEntity>> = repository.recentActivity(40)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun completeTask(task: TaskEntity) {
        viewModelScope.launch {
            repository.completeTask(task)
            reminderScheduler.cancel(task.id)
        }
    }
}
