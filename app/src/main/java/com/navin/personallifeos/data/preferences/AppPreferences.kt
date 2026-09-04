package com.navin.personallifeos.data.preferences

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore by preferencesDataStore(name = "personal_life_os_preferences")

@Singleton
class AppPreferences @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    private object Keys {
        val OnboardingComplete = booleanPreferencesKey("onboarding_complete")
        val ThemeMode = stringPreferencesKey("theme_mode")
    }

    val onboardingComplete: Flow<Boolean> = context.dataStore.data
        .map { it[Keys.OnboardingComplete] ?: false }

    val themeMode: Flow<String> = context.dataStore.data
        .map { it[Keys.ThemeMode] ?: "system" }

    suspend fun setOnboardingComplete(value: Boolean) {
        context.dataStore.edit { it[Keys.OnboardingComplete] = value }
    }

    suspend fun setThemeMode(value: String) {
        context.dataStore.edit { it[Keys.ThemeMode] = value }
    }
}
