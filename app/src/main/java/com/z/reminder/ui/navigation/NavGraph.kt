package com.z.reminder.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import com.z.reminder.data.model.Reminder
import com.z.reminder.data.repository.ReminderRepository
import com.z.reminder.ui.screens.addedit.AddEditReminderSheet
import com.z.reminder.ui.screens.completed.CompletedScreen
import com.z.reminder.ui.screens.settings.SettingsScreen
import com.z.reminder.ui.screens.today.TodayScreen
import com.z.reminder.ui.screens.upcoming.UpcomingScreen
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    repository: ReminderRepository = koinInject()
) {
    val coroutineScope = rememberCoroutineScope()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: Screen.Today.route

    var showAddEditSheet by remember { mutableStateOf(false) }
    var reminderToEdit by remember { mutableStateOf<Reminder?>(null) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    Box(modifier = modifier.fillMaxSize()) {
        NavHost(
            navController = navController,
            startDestination = Screen.Today.route,
            modifier = Modifier.fillMaxSize()
        ) {
            composable(Screen.Today.route) {
                TodayScreen(
                    onNavigateToSettings = { navController.navigate(Screen.Settings.route) },
                    onEditReminder = { reminder ->
                        reminderToEdit = reminder
                        showAddEditSheet = true
                    }
                )
            }

            composable(Screen.Upcoming.route) {
                UpcomingScreen(
                    onBack = { navController.popBackStack() },
                    onEditReminder = { reminder ->
                        reminderToEdit = reminder
                        showAddEditSheet = true
                    }
                )
            }

            composable(Screen.Completed.route) {
                CompletedScreen(
                    onBack = { navController.popBackStack() }
                )
            }

            composable(Screen.Settings.route) {
                SettingsScreen(
                    onBack = { navController.popBackStack() }
                )
            }
        }

        // Bottom Navigation Bar (Visible on Today, Completed, Upcoming)
        if (currentRoute != Screen.Settings.route) {
            ZBottomNav(
                currentRoute = currentRoute,
                onNavigateToToday = {
                    if (currentRoute != Screen.Today.route) {
                        navController.navigate(Screen.Today.route) {
                            popUpTo(Screen.Today.route) { inclusive = true }
                        }
                    }
                },
                onNavigateToCompleted = {
                    if (currentRoute != Screen.Completed.route) {
                        navController.navigate(Screen.Completed.route)
                    }
                },
                onNavigateToUpcoming = {
                    if (currentRoute != Screen.Upcoming.route) {
                        navController.navigate(Screen.Upcoming.route)
                    }
                },
                onAddClick = {
                    reminderToEdit = null
                    showAddEditSheet = true
                }
            )
        }

        // Add / Edit Modal Bottom Sheet
        if (showAddEditSheet) {
            AddEditReminderSheet(
                sheetState = sheetState,
                existingReminder = reminderToEdit,
                onDismiss = {
                    coroutineScope.launch {
                        sheetState.hide()
                        showAddEditSheet = false
                        reminderToEdit = null
                    }
                },
                onSave = { reminder ->
                    coroutineScope.launch {
                        if (reminder.id == 0L) {
                            repository.insertReminder(reminder)
                        } else {
                            repository.updateReminder(reminder)
                        }
                        sheetState.hide()
                        showAddEditSheet = false
                        reminderToEdit = null
                    }
                },
                onDelete = { reminder ->
                    coroutineScope.launch {
                        repository.deleteReminder(reminder)
                        sheetState.hide()
                        showAddEditSheet = false
                        reminderToEdit = null
                    }
                }
            )
        }
    }
}
