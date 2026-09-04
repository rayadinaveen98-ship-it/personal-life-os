package com.navin.personallifeos

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.navin.personallifeos.ui.navigation.PersonalLifeOsRoot
import com.navin.personallifeos.ui.theme.PersonalLifeOsTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PersonalLifeOsTheme {
                PersonalLifeOsRoot()
            }
        }
    }
}
