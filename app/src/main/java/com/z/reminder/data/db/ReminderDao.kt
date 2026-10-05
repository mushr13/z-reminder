package com.z.reminder.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.z.reminder.data.model.Reminder
import kotlinx.coroutines.flow.Flow

@Dao
interface ReminderDao {

    @Query("SELECT * FROM reminders WHERE status != 'COMPLETED' ORDER BY dueAt ASC")
    fun getAllActiveReminders(): Flow<List<Reminder>>

    @Query("SELECT * FROM reminders WHERE status = 'COMPLETED' ORDER BY completedAt DESC")
    fun getCompletedReminders(): Flow<List<Reminder>>

    @Query("SELECT * FROM reminders WHERE id = :id")
    suspend fun getReminderById(id: Long): Reminder?

    @Query("SELECT * FROM reminders WHERE dueAt BETWEEN :startOfDay AND :endOfDay AND status != 'COMPLETED' ORDER BY dueAt ASC")
    fun getRemindersForDay(startOfDay: Long, endOfDay: Long): Flow<List<Reminder>>

    @Query("SELECT * FROM reminders WHERE dueAt BETWEEN :startOfDay AND :endOfDay AND status != 'COMPLETED' ORDER BY dueAt ASC")
    suspend fun getRemindersForDaySnapshot(startOfDay: Long, endOfDay: Long): List<Reminder>

    @Query("SELECT * FROM reminders WHERE dueAt < :now AND status != 'COMPLETED' ORDER BY dueAt ASC")
    fun getOverdueReminders(now: Long): Flow<List<Reminder>>

    @Query("SELECT * FROM reminders WHERE dueAt >= :fromTime AND status != 'COMPLETED' ORDER BY dueAt ASC")
    fun getUpcomingReminders(fromTime: Long): Flow<List<Reminder>>

    @Query("SELECT * FROM reminders WHERE status = 'FIRING' OR (status = 'SNOOZED' AND snoozedUntil <= :now)")
    suspend fun getPendingAlerts(now: Long): List<Reminder>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReminder(reminder: Reminder): Long

    @Update
    suspend fun updateReminder(reminder: Reminder)

    @Delete
    suspend fun deleteReminder(reminder: Reminder)

    @Query("DELETE FROM reminders WHERE id = :id")
    suspend fun deleteReminderById(id: Long)

    @Query("UPDATE reminders SET status = 'COMPLETED', completedAt = :completedAt WHERE id = :id")
    suspend fun markCompleted(id: Long, completedAt: Long = System.currentTimeMillis())

    @Query("UPDATE reminders SET status = 'SCHEDULED', completedAt = null WHERE id = :id")
    suspend fun restoreCompleted(id: Long)

    @Query("UPDATE reminders SET status = 'SNOOZED', snoozedUntil = :snoozedUntil, snoozeCount = snoozeCount + 1 WHERE id = :id")
    suspend fun snoozeReminder(id: Long, snoozedUntil: Long)
}
