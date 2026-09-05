package com.navin.personallifeos

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.navin.personallifeos.ui.navigation.PersonalLifeOsRoot
import com.navin.personallifeos.ui.theme.PersonalLifeOsTheme
import com.navin.personallifeos.ui.viewmodel.AppEntryViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val entryViewModel: AppEntryViewModel = hiltViewModel()
            val themeMode by entryViewModel.themeMode.collectAsState()
            val systemDark = isSystemInDarkTheme()
            val darkTheme = when (themeMode) {
                "dark" -> true
                "light" -> false
                else -> systemDark
            }
            PersonalLifeOsTheme(darkTheme = darkTheme) {
                PersonalLifeOsRoot(entryViewModel = entryViewModel)
            }
        }
    }
}
