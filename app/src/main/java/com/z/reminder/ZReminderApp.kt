package com.z.reminder

import android.app.Application
import com.z.reminder.di.appModule
import com.z.reminder.places.OfficePresenceMonitor
import org.koin.android.ext.koin.androidContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.context.startKoin

class ZReminderApp : Application(), KoinComponent {

    private val officePresenceMonitor: OfficePresenceMonitor by inject()

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@ZReminderApp)
            modules(appModule)
        }

        // Start Wi-Fi presence monitor for office check-ins
        officePresenceMonitor.startMonitoring()
    }
}
