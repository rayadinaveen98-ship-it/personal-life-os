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

    fun finishOnboarding() {
        viewModelScope.launch { preferences.setOnboardingComplete(true) }
    }
}
