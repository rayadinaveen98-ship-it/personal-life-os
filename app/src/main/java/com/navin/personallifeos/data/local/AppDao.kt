package com.navin.personallifeos.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface AppDao {
    @Query("SELECT * FROM tasks ORDER BY completedAt IS NOT NULL, dueAt IS NULL, dueAt ASC, createdAt DESC")
    fun observeTasks(): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE completedAt IS NULL ORDER BY dueAt IS NULL, dueAt ASC, createdAt DESC")
    fun observePendingTasks(): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE id = :id LIMIT 1")
    fun observeTask(id: String): Flow<TaskEntity?>

    @Query("SELECT * FROM tasks WHERE completedAt IS NULL AND reminderAt IS NOT NULL AND reminderAt > :now ORDER BY reminderAt ASC")
    suspend fun pendingReminders(now: Long): List<TaskEntity>

    @Query("SELECT * FROM projects WHERE status = 'active' ORDER BY updatedAt DESC")
    fun observeActiveProjects(): Flow<List<ProjectEntity>>

    @Query("SELECT * FROM projects WHERE id = :id LIMIT 1")
    fun observeProject(id: String): Flow<ProjectEntity?>

    @Query("SELECT * FROM journal_entries ORDER BY createdAt DESC")
    fun observeJournal(): Flow<List<JournalEntryEntity>>

    @Query("SELECT * FROM journal_entries WHERE id = :id LIMIT 1")
    fun observeJournalEntry(id: String): Flow<JournalEntryEntity?>

    @Query("SELECT * FROM ideas ORDER BY createdAt DESC")
    fun observeIdeas(): Flow<List<IdeaEntity>>

    @Query("SELECT * FROM ideas WHERE id = :id LIMIT 1")
    fun observeIdea(id: String): Flow<IdeaEntity?>

    @Query("SELECT * FROM activity_events ORDER BY occurredAt DESC LIMIT :limit")
    fun observeRecentActivity(limit: Int = 100): Flow<List<ActivityEventEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertTask(task: TaskEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertProject(project: ProjectEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertJournal(entry: JournalEntryEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertIdea(idea: IdeaEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertActivity(event: ActivityEventEntity)

    @Query("DELETE FROM tasks WHERE id = :id")
    suspend fun deleteTask(id: String)

    @Query("DELETE FROM journal_entries WHERE id = :id")
    suspend fun deleteJournalEntry(id: String)

    @Query("DELETE FROM ideas WHERE id = :id")
    suspend fun deleteIdea(id: String)
}
