package com.navin.personallifeos.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "projects")
data class ProjectEntity(
    @PrimaryKey val id: String,
    val title: String,
    val description: String = "",
    val status: String = "active",
    val currentMilestone: String = "",
    val createdAt: Long,
    val updatedAt: Long,
)

@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey val id: String,
    val title: String,
    val notes: String = "",
    val projectId: String? = null,
    val dueAt: Long? = null,
    val reminderAt: Long? = null,
    val completedAt: Long? = null,
    val priority: Int = 0,
    val deferCount: Int = 0,
    val createdAt: Long,
    val updatedAt: Long,
)

@Entity(tableName = "journal_entries")
data class JournalEntryEntity(
    @PrimaryKey val id: String,
    val title: String,
    val body: String,
    val mood: String? = null,
    val createdAt: Long,
    val updatedAt: Long,
)

@Entity(tableName = "ideas")
data class IdeaEntity(
    @PrimaryKey val id: String,
    val title: String,
    val body: String,
    val projectId: String? = null,
    val createdAt: Long,
    val updatedAt: Long,
)

@Entity(tableName = "activity_events")
data class ActivityEventEntity(
    @PrimaryKey val id: String,
    val type: String,
    val title: String,
    val entityId: String? = null,
    val occurredAt: Long,
    val metadataJson: String = "{}",
)
