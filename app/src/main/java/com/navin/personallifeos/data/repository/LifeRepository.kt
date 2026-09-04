package com.navin.personallifeos.data.repository

import com.navin.personallifeos.data.local.ActivityEventEntity
import com.navin.personallifeos.data.local.AppDao
import com.navin.personallifeos.data.local.IdeaEntity
import com.navin.personallifeos.data.local.JournalEntryEntity
import com.navin.personallifeos.data.local.ProjectEntity
import com.navin.personallifeos.data.local.TaskEntity
import kotlinx.coroutines.flow.Flow
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LifeRepository @Inject constructor(
    private val dao: AppDao,
) {
    fun tasks(): Flow<List<TaskEntity>> = dao.observeTasks()
    fun pendingTasks(): Flow<List<TaskEntity>> = dao.observePendingTasks()
    fun activeProjects(): Flow<List<ProjectEntity>> = dao.observeActiveProjects()
    fun journal(): Flow<List<JournalEntryEntity>> = dao.observeJournal()
    fun ideas(): Flow<List<IdeaEntity>> = dao.observeIdeas()
    fun recentActivity(limit: Int = 100): Flow<List<ActivityEventEntity>> = dao.observeRecentActivity(limit)

    suspend fun pendingReminders(now: Long = System.currentTimeMillis()): List<TaskEntity> = dao.pendingReminders(now)

    suspend fun saveTask(task: TaskEntity, eventType: String = "task_created") {
        dao.upsertTask(task)
        dao.insertActivity(
            ActivityEventEntity(
                id = UUID.randomUUID().toString(),
                type = eventType,
                title = task.title,
                entityId = task.id,
                occurredAt = System.currentTimeMillis(),
            ),
        )
    }

    suspend fun completeTask(task: TaskEntity) {
        val now = System.currentTimeMillis()
        dao.upsertTask(task.copy(completedAt = now, updatedAt = now))
        dao.insertActivity(
            ActivityEventEntity(
                id = UUID.randomUUID().toString(),
                type = "task_completed",
                title = task.title,
                entityId = task.id,
                occurredAt = now,
            ),
        )
    }

    suspend fun saveProject(project: ProjectEntity) = dao.upsertProject(project)

    suspend fun saveJournal(entry: JournalEntryEntity) {
        dao.upsertJournal(entry)
        dao.insertActivity(
            ActivityEventEntity(
                id = UUID.randomUUID().toString(),
                type = "journal",
                title = entry.title,
                entityId = entry.id,
                occurredAt = entry.createdAt,
            ),
        )
    }

    suspend fun saveIdea(idea: IdeaEntity) {
        dao.upsertIdea(idea)
        dao.insertActivity(
            ActivityEventEntity(
                id = UUID.randomUUID().toString(),
                type = "idea",
                title = idea.title,
                entityId = idea.id,
                occurredAt = idea.createdAt,
            ),
        )
    }
}
