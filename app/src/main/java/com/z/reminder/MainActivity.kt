package com.z.reminder

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.z.reminder.data.repository.SettingsRepository
import com.z.reminder.ui.navigation.NavGraph
import com.z.reminder.ui.theme.AppThemeMode
import com.z.reminder.ui.theme.ZReminderTheme
import org.koin.android.ext.android.inject

class MainActivity : ComponentActivity() {

    private val settingsRepository: SettingsRepository by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val openAdd = intent?.getBooleanExtra("extra_action_add_reminder", false) ?: false

        setContent {
            val themeMode by settingsRepository.themeModeFlow.collectAsState(initial = AppThemeMode.SYSTEM)

            ZReminderTheme(themeMode = themeMode) {
                Surface(modifier = Modifier.fillMaxSize()) {
                    val navController = rememberNavController()
                    NavGraph(navController = navController, initialOpenAddSheet = openAdd)
                }
            }
        }
    }
}
