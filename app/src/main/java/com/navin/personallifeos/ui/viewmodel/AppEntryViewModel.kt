package com.navin.personallifeos.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.navin.personallifeos.data.preferences.AppPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class AppEntryViewModel @Inject constructor(
    private val preferences: AppPreferences,
) : ViewModel() {
    val onboardingComplete: StateFlow<Boolean> = preferences.onboardingComplete
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), false)

    val preferredName: StateFlow<String> = preferences.preferredName
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), "Navin")

    fun finishOnboarding(
        name: String,
        focusAreas: Set<String>,
        lifeAreas: Set<String>,
        morningBrief: Boolean,
        eveningReflection: Boolean,
    ) {
        viewModelScope.launch {
            preferences.completeOnboarding(
                name = name,
                selectedFocusAreas = focusAreas,
                selectedLifeAreas = lifeAreas,
                enableMorningBrief = morningBrief,
                enableEveningReflection = eveningReflection,
            )
        }
    }
}
