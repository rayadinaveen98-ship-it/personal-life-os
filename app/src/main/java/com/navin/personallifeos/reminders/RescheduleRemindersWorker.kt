package com.navin.personallifeos.reminders

import android.content.Context
import androidx.room.Room
import androidx.work.CoroutineWorker
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkerParameters
import com.navin.personallifeos.data.local.AppDatabase

class RescheduleRemindersWorker(
    appContext: Context,
    workerParams: WorkerParameters,
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result = runCatching {
        val db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "personal-life-os.db",
        ).fallbackToDestructiveMigration().build()

        val scheduler = ReminderScheduler(applicationContext)
        db.appDao().pendingReminders(System.currentTimeMillis()).forEach { task ->
            val trigger = task.reminderAt ?: return@forEach
            scheduler.scheduleExact(task.id, task.title, trigger)
        }
        db.close()
        Result.success()
    }.getOrElse { Result.retry() }

    companion object {
        fun request(): OneTimeWorkRequest = OneTimeWorkRequestBuilder<RescheduleRemindersWorker>().build()
    }
}
