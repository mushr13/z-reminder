package com.z.reminder.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.z.reminder.data.model.Place
import com.z.reminder.data.model.Reminder
import com.z.reminder.data.model.ReminderCategory

@Database(
    entities = [
        Reminder::class,
        Place::class,
        ReminderCategory::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun reminderDao(): ReminderDao
    abstract fun placeDao(): PlaceDao
}
