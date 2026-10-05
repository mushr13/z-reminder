package com.z.reminder.ui.navigation

sealed class Screen(val route: String) {
    object Today : Screen("today")
    object Completed : Screen("completed")
    object Upcoming : Screen("upcoming")
    object Settings : Screen("settings")
}
