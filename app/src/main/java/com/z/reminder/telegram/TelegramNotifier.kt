package com.z.reminder.telegram

import android.util.Log
import com.z.reminder.data.model.Reminder
import com.z.reminder.data.repository.SettingsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

class TelegramNotifier(private val settingsRepository: SettingsRepository) {

    companion object {
        private const val TAG = "TelegramNotifier"
    }

    private val timeFormatter = DateTimeFormatter.ofPattern("hh:mm a", Locale.getDefault())
    private val dateFormatter = DateTimeFormatter.ofPattern("EEE, MMM d", Locale.getDefault())

    suspend fun sendMessage(text: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val enabled = settingsRepository.telegramEnabledFlow.first()
            if (!enabled) {
                Log.d(TAG, "Telegram notifications disabled in settings.")
                return@withContext Result.success(Unit)
            }

            val token = settingsRepository.telegramBotTokenFlow.first()
            val chatId = settingsRepository.telegramChatIdFlow.first()

            if (token.isBlank() || chatId.isBlank()) {
                Log.w(TAG, "Missing token or chatId.")
                return@withContext Result.failure(IllegalStateException("Bot token or Chat ID is empty"))
            }

            val url = URL("https://api.telegram.org/bot$token/sendMessage")
            val conn = (url.openConnection() as HttpURLConnection).apply {
                requestMethod = "POST"
                doOutput = true
                connectTimeout = 8000
                readTimeout = 8000
                setRequestProperty("Content-Type", "application/json; charset=UTF-8")
                setRequestProperty("Accept", "application/json")
            }

            val payload = JSONObject().apply {
                put("chat_id", chatId)
                put("text", text)
                put("parse_mode", "Markdown")
            }

            OutputStreamWriter(conn.outputStream, Charsets.UTF_8).use { writer ->
                writer.write(payload.toString())
                writer.flush()
            }

            val responseCode = conn.responseCode
            if (responseCode in 200..299) {
                Log.i(TAG, "Successfully sent Telegram notification to chat $chatId")
                Result.success(Unit)
            } else {
                val errorBody = conn.errorStream?.bufferedReader()?.use { it.readText() } ?: "HTTP $responseCode"
                Log.e(TAG, "Telegram API error: $errorBody")
                Result.failure(Exception("Telegram API error: HTTP $responseCode: $errorBody"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to send Telegram message", e)
            Result.failure(e)
        }
    }

    suspend fun notifyReminderCreated(reminder: Reminder) {
        val dueZoned = Instant.ofEpochMilli(reminder.dueAt).atZone(ZoneId.systemDefault())
        val formattedTime = dueZoned.format(timeFormatter)
        val formattedDate = dueZoned.format(dateFormatter)

        val sb = StringBuilder()
        sb.append("🔔 *New Z Reminder Created*\n\n")
        sb.append("📌 *${escapeMarkdown(reminder.title)}*\n")
        sb.append("⏰ *Due:* $formattedTime ($formattedDate)\n")
        if (!reminder.notes.isNullOrBlank()) {
            sb.append("📝 *Notes:* ${escapeMarkdown(reminder.notes)}\n")
        }
        sb.append("\n_Synced from Z Reminder on Honor_")

        sendMessage(sb.toString())
    }

    suspend fun notifyReminderAlert(reminder: Reminder) {
        val sb = StringBuilder()
        sb.append("🚨 *Z Reminder Alert!*\n\n")
        sb.append("📌 *${escapeMarkdown(reminder.title)}*\n")
        sb.append("⏰ *Status:* Due Now!\n")
        if (!reminder.notes.isNullOrBlank()) {
            sb.append("📝 *Notes:* ${escapeMarkdown(reminder.notes)}\n")
        }
        sb.append("\n_Ringing on phone lockscreen_")

        sendMessage(sb.toString())
    }

    private fun escapeMarkdown(text: String): String {
        return text.replace("_", "\\_")
            .replace("*", "\\*")
            .replace("[", "\\[")
            .replace("`", "\\`")
    }
}
