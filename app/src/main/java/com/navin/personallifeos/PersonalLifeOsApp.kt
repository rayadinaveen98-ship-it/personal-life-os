package com.navin.personallifeos

import android.app.Application
import androidx.work.WorkManager
import com.navin.personallifeos.reminders.RescheduleRemindersWorker
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class PersonalLifeOsApp : Application() {
    override fun onCreate() {
        super.onCreate()
        // Reconcile saved reminders on every cold app start. This also catches reminders
        // that were saved before exact-alarm access was granted.
        WorkManager.getInstance(this).enqueue(RescheduleRemindersWorker.request())
    }
}
