package com.navin.personallifeos.data.preferences

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
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
        val PreferredName = stringPreferencesKey("preferred_name")
        val FocusAreas = stringSetPreferencesKey("focus_areas")
        val LifeAreas = stringSetPreferencesKey("life_areas")
        val MorningBrief = booleanPreferencesKey("morning_brief")
        val EveningReflection = booleanPreferencesKey("evening_reflection")
    }

    val onboardingComplete: Flow<Boolean> = context.dataStore.data
        .map { it[Keys.OnboardingComplete] ?: false }

    val themeMode: Flow<String> = context.dataStore.data
        .map { it[Keys.ThemeMode] ?: "system" }

    val preferredName: Flow<String> = context.dataStore.data
        .map { it[Keys.PreferredName]?.takeIf(String::isNotBlank) ?: "Navin" }

    val focusAreas: Flow<Set<String>> = context.dataStore.data
        .map { it[Keys.FocusAreas] ?: emptySet() }

    val lifeAreas: Flow<Set<String>> = context.dataStore.data
        .map { it[Keys.LifeAreas] ?: emptySet() }

    val morningBrief: Flow<Boolean> = context.dataStore.data
        .map { it[Keys.MorningBrief] ?: true }

    val eveningReflection: Flow<Boolean> = context.dataStore.data
        .map { it[Keys.EveningReflection] ?: true }

    suspend fun completeOnboarding(
        name: String,
        selectedFocusAreas: Set<String>,
        selectedLifeAreas: Set<String>,
        enableMorningBrief: Boolean,
        enableEveningReflection: Boolean,
    ) {
        context.dataStore.edit {
            it[Keys.PreferredName] = name.ifBlank { "Navin" }
            it[Keys.FocusAreas] = selectedFocusAreas
            it[Keys.LifeAreas] = selectedLifeAreas
            it[Keys.MorningBrief] = enableMorningBrief
            it[Keys.EveningReflection] = enableEveningReflection
            it[Keys.OnboardingComplete] = true
        }
    }

    suspend fun setOnboardingComplete(value: Boolean) {
        context.dataStore.edit { it[Keys.OnboardingComplete] = value }
    }

    suspend fun setThemeMode(value: String) {
        context.dataStore.edit { it[Keys.ThemeMode] = value }
    }

    suspend fun setPreferredName(value: String) {
        context.dataStore.edit { it[Keys.PreferredName] = value.trim().ifBlank { "Navin" } }
    }

    suspend fun setMorningBrief(value: Boolean) {
        context.dataStore.edit { it[Keys.MorningBrief] = value }
    }

    suspend fun setEveningReflection(value: Boolean) {
        context.dataStore.edit { it[Keys.EveningReflection] = value }
    }

    suspend fun setFocusAreas(value: Set<String>) {
        context.dataStore.edit { it[Keys.FocusAreas] = value }
    }

    suspend fun setLifeAreas(value: Set<String>) {
        context.dataStore.edit { it[Keys.LifeAreas] = value }
    }
}
