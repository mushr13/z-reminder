package com.z.reminder.alert

import android.app.KeyguardManager
import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.NotificationsActive
import androidx.compose.material.icons.rounded.Snooze
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.z.reminder.alarm.AlarmScheduler
import com.z.reminder.data.model.Reminder
import com.z.reminder.data.repository.ReminderRepository
import com.z.reminder.notification.NotificationHelper
import com.z.reminder.ui.theme.ButtonShape
import com.z.reminder.ui.theme.ChipShape
import com.z.reminder.ui.theme.SuccessGreen
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class FullScreenAlertActivity : ComponentActivity() {

    private val repository: ReminderRepository by inject()
    private val notificationHelper: NotificationHelper by inject()
    private val alarmScheduler: AlarmScheduler by inject()

    private var mediaPlayer: MediaPlayer? = null
    private var vibrator: Vibrator? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Lockscreen awakening flags
        setupLockscreenFlags()

        val reminderId = intent.getLongExtra(AlarmScheduler.EXTRA_REMINDER_ID, -1L)
        val isSimulation = intent.getBooleanExtra("extra_is_simulation", false)

        // Start alarm audio and vibration
        startAlarmAudioAndVibration()

        setContent {
            var reminder by remember { mutableStateOf<Reminder?>(null) }
            val coroutineScope = rememberCoroutineScope()

            LaunchedEffect(reminderId) {
                if (isSimulation) {
                    reminder = Reminder(
                        id = 999999,
                        title = "Sample Full-Screen Alert",
                        notes = "This is a live preview test of your full-screen alarm screen.",
                        dueAt = System.currentTimeMillis()
                    )
                } else if (reminderId != -1L) {
                    reminder = repository.getReminderById(reminderId)
                }
            }

            val preset = AlertBackgrounds.getPresetById(reminder?.backgroundId)

            FullScreenAlertContent(
                reminder = reminder,
                preset = preset,
                onComplete = {
                    stopAudioAndVibration()
                    coroutineScope.launch {
                        if (!isSimulation && reminderId != -1L) {
                            repository.markCompleted(reminderId)
                        }
                        finish()
                    }
                },
                onSnooze = { minutes ->
                    stopAudioAndVibration()
                    coroutineScope.launch {
                        if (!isSimulation && reminderId != -1L) {
                            val snoozeUntil = System.currentTimeMillis() + (minutes * 60 * 1000L)
                            repository.snoozeReminder(reminderId, snoozeUntil)
                            notificationHelper.cancelNotification(reminderId)
                        }
                        finish()
                    }
                },
                onDismiss = {
                    // Dismiss keeps the nagging active, but silences the current ringing
                    stopAudioAndVibration()
                    finish()
                }
            )
        }
    }

    private fun setupLockscreenFlags() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true)
            setTurnScreenOn(true)
            val keyguardManager = getSystemService(Context.KEYGUARD_SERVICE) as? KeyguardManager
            keyguardManager?.requestDismissKeyguard(this, null)
        } else {
            @Suppress("DEPRECATION")
            window.addFlags(
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
                        WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON or
                        WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
            )
        }
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }

    private fun startAlarmAudioAndVibration() {
        try {
            val alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
                ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

            mediaPlayer = MediaPlayer().apply {
                setDataSource(applicationContext, alarmUri)
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_ALARM)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build()
                )
                isLooping = true
                prepare()
                start()
            }

            vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vm = getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
                vm.defaultVibrator
            } else {
                @Suppress("DEPRECATION")
                getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            }

            val pattern = longArrayOf(0, 800, 400, 800, 400)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator?.vibrate(VibrationEffect.createWaveform(pattern, 0))
            } else {
                @Suppress("DEPRECATION")
                vibrator?.vibrate(pattern, 0)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun stopAudioAndVibration() {
        try {
            mediaPlayer?.stop()
            mediaPlayer?.release()
            mediaPlayer = null

            vibrator?.cancel()
            vibrator = null
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onDestroy() {
        stopAudioAndVibration()
        super.onDestroy()
    }
}

@Composable
fun FullScreenAlertContent(
    reminder: Reminder?,
    preset: AlertBackgroundPreset,
    onComplete: () -> Unit,
    onSnooze: (Int) -> Unit,
    onDismiss: () -> Unit
) {
    var currentTimeString by remember { mutableStateOf("") }
    var currentDateString by remember { mutableStateOf("") }
    var showSnoozeOptions by remember { mutableStateOf(false) }

    val timeFormat = remember { SimpleDateFormat("h:mm", Locale.getDefault()) }
    val amPmFormat = remember { SimpleDateFormat("a", Locale.getDefault()) }
    val dateFormat = remember { SimpleDateFormat("EEEE, d MMMM", Locale.getDefault()) }

    // Live clock ticker
    LaunchedEffect(Unit) {
        while (true) {
            val now = Date()
            currentTimeString = timeFormat.format(now)
            currentDateString = dateFormat.format(now)
            delay(1000)
        }
    }

    val textColor = AlertBackgrounds.getHighContrastTextColor(preset.colors[0])
    val subtextColor = textColor.copy(alpha = 0.75f)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AlertBackgrounds.getBackgroundBrush(preset))
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(24.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxSize()
        ) {
            // Top Section: Live Clock & Date
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(top = 32.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = currentTimeString,
                        style = MaterialTheme.typography.displayLarge.copy(
                            fontSize = 68.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = (-2).sp
                        ),
                        color = textColor
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = amPmFormat.format(Date()).uppercase(),
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = subtextColor,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                }

                Text(
                    text = currentDateString,
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium),
                    color = subtextColor
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Firing Badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(textColor.copy(alpha = 0.15f))
                        .padding(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Rounded.NotificationsActive,
                            contentDescription = null,
                            tint = textColor,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Reminder Alarm",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                            color = textColor
                        )
                    }
                }
            }

            // Center Section: Reminder Title & Notes (Protected with WCAG Scrim)
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(
                        if (preset.isDarkBackground) Color.Black.copy(alpha = 0.35f)
                        else Color.White.copy(alpha = 0.45f)
                    )
                    .border(
                        1.dp,
                        textColor.copy(alpha = 0.2f),
                        RoundedCornerShape(24.dp)
                    )
                    .padding(horizontal = 24.dp, vertical = 32.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = reminder?.title ?: "Reminder Alert",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 28.sp
                        ),
                        color = textColor,
                        textAlign = TextAlign.Center
                    )

                    if (!reminder?.notes.isNullOrBlank()) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = reminder?.notes ?: "",
                            style = MaterialTheme.typography.bodyLarge,
                            color = subtextColor,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            // Bottom Section: Action Buttons
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Snooze Options Row (if opened)
                AnimatedVisibility(visible = showSnoozeOptions) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp)
                    ) {
                        listOf(10 to "10m", 15 to "15m", 30 to "30m", 60 to "1h").forEach { (mins, label) ->
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(ChipShape)
                                    .background(textColor.copy(alpha = 0.2f))
                                    .clickable { onSnooze(mins) }
                                    .padding(vertical = 10.dp)
                            ) {
                                Text(
                                    text = label,
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                    color = textColor
                                )
                            }
                        }
                    }
                }

                // 1. Primary Action: Complete
                Button(
                    onClick = onComplete,
                    shape = ButtonShape,
                    colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                ) {
                    Icon(Icons.Rounded.Check, contentDescription = null, tint = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Mark Completed",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // 2. Secondary Row: Snooze & Dismiss
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedButton(
                        onClick = { showSnoozeOptions = !showSnoozeOptions },
                        shape = ButtonShape,
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = textColor),
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp)
                    ) {
                        Icon(Icons.Rounded.Snooze, contentDescription = null, tint = textColor)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "Snooze", fontWeight = FontWeight.SemiBold, color = textColor)
                    }

                    OutlinedButton(
                        onClick = onDismiss,
                        shape = ButtonShape,
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = textColor),
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp)
                    ) {
                        Icon(Icons.Rounded.Close, contentDescription = null, tint = textColor)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "Dismiss", fontWeight = FontWeight.SemiBold, color = textColor)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}
