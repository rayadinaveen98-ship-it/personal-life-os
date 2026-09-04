package com.navin.personallifeos.reminders

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReminderScheduler @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    fun canScheduleExact(): Boolean {
        val alarmManager = context.getSystemService(AlarmManager::class.java)
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.S || alarmManager.canScheduleExactAlarms()
    }

    fun scheduleExact(reminderId: String, title: String, triggerAtMillis: Long): Boolean {
        val alarmManager = context.getSystemService(AlarmManager::class.java)
        if (!canScheduleExact()) return false

        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            triggerAtMillis,
            pendingIntent(reminderId, title),
        )
        return true
    }

    fun cancel(reminderId: String) {
        context.getSystemService(AlarmManager::class.java)
            .cancel(pendingIntent(reminderId, ""))
    }

    private fun pendingIntent(reminderId: String, title: String): PendingIntent {
        val intent = Intent(context, ReminderReceiver::class.java)
            .putExtra(ReminderReceiver.EXTRA_REMINDER_ID, reminderId)
            .putExtra(ReminderReceiver.EXTRA_TITLE, title)

        return PendingIntent.getBroadcast(
            context,
            reminderId.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
    }
}
