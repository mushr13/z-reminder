package com.z.reminder

import android.app.Application
import com.z.reminder.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class ZReminderApp : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@ZReminderApp)
            modules(appModule)
        }
    }
}
