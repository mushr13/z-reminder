package com.z.reminder.di

import androidx.room.Room
import com.z.reminder.data.db.AppDatabase
import com.z.reminder.data.repository.ReminderRepository
import com.z.reminder.data.repository.SettingsRepository
import com.z.reminder.ui.screens.completed.CompletedViewModel
import com.z.reminder.ui.screens.settings.SettingsViewModel
import com.z.reminder.ui.screens.today.TodayViewModel
import com.z.reminder.ui.screens.upcoming.UpcomingViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    // Database & DAOs
    single {
        Room.databaseBuilder(
            androidContext(),
            AppDatabase::class.java,
            "z_reminder.db"
        ).fallbackToDestructiveMigration().build()
    }

    single { get<AppDatabase>().reminderDao() }
    single { get<AppDatabase>().placeDao() }

    // Repositories
    single { ReminderRepository(get()) }
    single { SettingsRepository(androidContext()) }

    // ViewModels
    viewModel { TodayViewModel(get()) }
    viewModel { UpcomingViewModel(get()) }
    viewModel { CompletedViewModel(get()) }
    viewModel { SettingsViewModel(get()) }
}
