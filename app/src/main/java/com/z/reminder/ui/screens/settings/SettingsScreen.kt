package com.z.reminder.ui.screens.settings

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.DarkMode
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.NotificationsActive
import androidx.compose.material.icons.rounded.Schedule
import androidx.compose.material.icons.rounded.Send
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.z.reminder.ui.theme.AppThemeMode
import com.z.reminder.ui.theme.CardShape
import com.z.reminder.ui.theme.ChipShape
import com.z.reminder.ui.theme.PrimaryViolet
import org.koin.androidx.compose.koinViewModel

@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SettingsViewModel = koinViewModel()
) {
    val currentTheme by viewModel.themeMode.collectAsState()
    val nagInterval by viewModel.nagInterval.collectAsState()
    val telegramEnabled by viewModel.telegramEnabled.collectAsState()
    val telegramBotToken by viewModel.telegramBotToken.collectAsState()
    val telegramChatId by viewModel.telegramChatId.collectAsState()
    val presets = viewModel.getQuickPresets()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
    ) {
        // Top Header
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.Rounded.ArrowBack,
                    contentDescription = "Back",
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }

            Text(
                text = "Settings",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp
                ),
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(start = 8.dp)
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Theme Mode Card
            Card(
                shape = CardShape,
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Rounded.DarkMode,
                            contentDescription = null,
                            tint = PrimaryViolet,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Theme",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        ThemeOptionChip(
                            title = "System",
                            isSelected = currentTheme == AppThemeMode.SYSTEM,
                            onClick = { viewModel.setThemeMode(AppThemeMode.SYSTEM) },
                            modifier = Modifier.weight(1f)
                        )
                        ThemeOptionChip(
                            title = "Lavender",
                            isSelected = currentTheme == AppThemeMode.LIGHT,
                            onClick = { viewModel.setThemeMode(AppThemeMode.LIGHT) },
                            modifier = Modifier.weight(1f)
                        )
                        ThemeOptionChip(
                            title = "Dark",
                            isSelected = currentTheme == AppThemeMode.DARK,
                            onClick = { viewModel.setThemeMode(AppThemeMode.DARK) },
                            modifier = Modifier.weight(1f)
                        )
                        ThemeOptionChip(
                            title = "AMOLED",
                            isSelected = currentTheme == AppThemeMode.AMOLED,
                            onClick = { viewModel.setThemeMode(AppThemeMode.AMOLED) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // Navigation Badges Card
            val badgesEnabled by viewModel.badgesEnabled.collectAsState()
            Card(
                shape = CardShape,
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Bottom Bar Badges",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Show count badges on Completed & Upcoming tabs",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Switch(
                        checked = badgesEnabled,
                        onCheckedChange = { viewModel.setBadgesEnabled(it) },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = PrimaryViolet
                        )
                    )
                }
            }

            // Quick Presets Overview Card
            Card(
                shape = CardShape,
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Rounded.Schedule,
                            contentDescription = null,
                            tint = PrimaryViolet,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Quick Presets",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    presets.forEach { preset ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        ) {
                            Text(
                                text = preset.title,
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            val targetDesc = if (preset.id == "plus_1_hour") "+60 Minutes"
                            else String.format("%02d:%02d (+%dm stagger)", preset.targetHour, preset.targetMinute, preset.staggerMinutes)

                            Text(
                                text = targetDesc,
                                style = MaterialTheme.typography.labelMedium,
                                color = PrimaryViolet
                            )
                        }
                    }
                }
            }

            // Nag Interval Card
            Card(
                shape = CardShape,
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Rounded.NotificationsActive,
                            contentDescription = null,
                            tint = PrimaryViolet,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Nagging Interval",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        listOf(15, 30, 45, 60).forEach { mins ->
                            ThemeOptionChip(
                                title = "$mins m",
                                isSelected = nagInterval == mins,
                                onClick = { viewModel.setNagInterval(mins) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }

            // Office Presence & Wi-Fi Check-in Card
            val places by viewModel.places.collectAsState()
            val officePlace = places.firstOrNull { it.type == com.z.reminder.data.model.PlaceType.OFFICE }
            var editingSsid by androidx.compose.runtime.remember(officePlace?.wifiSsids) {
                androidx.compose.runtime.mutableStateOf(officePlace?.wifiSsids ?: "")
            }

            Card(
                shape = CardShape,
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Rounded.Schedule,
                            contentDescription = null,
                            tint = PrimaryViolet,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Office Wi-Fi & Check-in",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    androidx.compose.material3.OutlinedTextField(
                        value = editingSsid,
                        onValueChange = { editingSsid = it },
                        label = { Text("Office Wi-Fi Name (SSID)") },
                        placeholder = { Text("e.g., Office_5G, MyWorkplace") },
                        singleLine = true,
                        shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    androidx.compose.material3.Button(
                        onClick = { viewModel.saveOfficeWifi(editingSsid) },
                        shape = com.z.reminder.ui.theme.ButtonShape,
                        colors = androidx.compose.material3.ButtonDefaults.buttonColors(containerColor = PrimaryViolet),
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Text("Save Office Wi-Fi", fontWeight = FontWeight.SemiBold)
                    }
                }
            }

            // Full-Screen Alert Screen Showcase & Simulator
            val context = androidx.compose.ui.platform.LocalContext.current
            Card(
                shape = CardShape,
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Rounded.NotificationsActive,
                            contentDescription = null,
                            tint = PrimaryViolet,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Full-Screen Alert Experience",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    var testScheduledMessage by androidx.compose.runtime.remember {
                        androidx.compose.runtime.mutableStateOf<String?>(null)
                    }

                    androidx.compose.material3.Button(
                        onClick = {
                            val intent = android.content.Intent(context, com.z.reminder.alert.FullScreenAlertActivity::class.java).apply {
                                flags = android.content.Intent.FLAG_ACTIVITY_NEW_TASK
                                putExtra("extra_is_simulation", true)
                            }
                            context.startActivity(intent)
                        },
                        shape = com.z.reminder.ui.theme.ButtonShape,
                        colors = androidx.compose.material3.ButtonDefaults.buttonColors(containerColor = PrimaryViolet),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.NotificationsActive,
                            contentDescription = null,
                            tint = Color.White
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Test Full-Screen Alert Now",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    androidx.compose.material3.OutlinedButton(
                        onClick = {
                            viewModel.scheduleTestAlarmInOneMinute {
                                testScheduledMessage = "Alarm set for 1 min from now! Lock your screen now to test."
                            }
                        },
                        shape = com.z.reminder.ui.theme.ButtonShape,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Schedule,
                            contentDescription = null,
                            tint = PrimaryViolet
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "1-Min Lockscreen Test",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                            color = PrimaryViolet
                        )
                    }

                    testScheduledMessage?.let { msg ->
                        Text(
                            text = "✓ $msg",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = com.z.reminder.ui.theme.SuccessGreen
                            ),
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                }
            }

            // Permission Center & Honor MagicOS Hardening Card
            val permissions = androidx.compose.runtime.remember { viewModel.getPermissions() }
            Card(
                shape = CardShape,
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Rounded.Info,
                            contentDescription = null,
                            tint = PrimaryViolet,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Permission Center & Honor Hardening",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Honor MagicOS / Android 16 requires exact alarms, battery exemption, and auto-launch to guarantee 100% ringing reliability.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    permissions.forEach { perm ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = if (perm.isGranted) "✓ " else "⚠ ",
                                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                        color = if (perm.isGranted) com.z.reminder.ui.theme.SuccessGreen else com.z.reminder.ui.theme.OverdueRed
                                    )
                                    Text(
                                        text = perm.title,
                                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                                Text(
                                    text = perm.description,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(start = 16.dp, top = 2.dp)
                                )
                            }

                            androidx.compose.material3.OutlinedButton(
                                onClick = {
                                    try {
                                        context.startActivity(perm.intentAction())
                                    } catch (e: Exception) {
                                        e.printStackTrace()
                                    }
                                },
                                shape = com.z.reminder.ui.theme.ButtonShape,
                                modifier = Modifier
                                    .padding(start = 8.dp)
                                    .height(36.dp)
                            ) {
                                Text(
                                    text = if (perm.isGranted) "Open" else "Grant",
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                                )
                            }
                        }
                    }
                }
            }

            // Telegram Bot Link Card
            var editingToken by androidx.compose.runtime.remember(telegramBotToken) {
                androidx.compose.runtime.mutableStateOf(telegramBotToken)
            }
            var editingChatId by androidx.compose.runtime.remember(telegramChatId) {
                androidx.compose.runtime.mutableStateOf(telegramChatId)
            }
            var telegramFeedback by androidx.compose.runtime.remember {
                androidx.compose.runtime.mutableStateOf<Pair<Boolean, String>?>(null)
            }

            Card(
                shape = CardShape,
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Rounded.Send,
                                contentDescription = null,
                                tint = PrimaryViolet,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Telegram Bot Link",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        Switch(
                            checked = telegramEnabled,
                            onCheckedChange = { viewModel.setTelegramEnabled(it) },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = PrimaryViolet
                            )
                        )
                    }

                    if (telegramEnabled) {
                        Spacer(modifier = Modifier.height(12.dp))

                        androidx.compose.material3.OutlinedTextField(
                            value = editingToken,
                            onValueChange = { editingToken = it },
                            label = { Text("Bot Token") },
                            singleLine = true,
                            shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        androidx.compose.material3.OutlinedTextField(
                            value = editingChatId,
                            onValueChange = { editingChatId = it },
                            label = { Text("Admin Chat ID") },
                            singleLine = true,
                            shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            androidx.compose.material3.Button(
                                onClick = {
                                    viewModel.saveTelegramConfig(editingToken, editingChatId)
                                    telegramFeedback = Pair(true, "Settings saved!")
                                },
                                shape = com.z.reminder.ui.theme.ButtonShape,
                                colors = androidx.compose.material3.ButtonDefaults.buttonColors(containerColor = PrimaryViolet),
                                modifier = Modifier.weight(1f).height(44.dp)
                            ) {
                                Text("Save Link", fontWeight = FontWeight.Bold)
                            }

                            androidx.compose.material3.OutlinedButton(
                                onClick = {
                                    viewModel.saveTelegramConfig(editingToken, editingChatId)
                                    viewModel.sendTestTelegramMessage { success, msg ->
                                        telegramFeedback = Pair(success, msg)
                                    }
                                },
                                shape = com.z.reminder.ui.theme.ButtonShape,
                                modifier = Modifier.weight(1f).height(44.dp)
                            ) {
                                Text("Send Test", fontWeight = FontWeight.Bold)
                            }
                        }

                        telegramFeedback?.let { (success, msg) ->
                            Text(
                                text = if (success) "✓ $msg" else "⚠ $msg",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = if (success) com.z.reminder.ui.theme.SuccessGreen else com.z.reminder.ui.theme.OverdueRed
                                ),
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }
                    }
                }
            }

            // About Card
            Card(
                shape = CardShape,
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.7f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(16.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Info,
                        contentDescription = null,
                        tint = PrimaryViolet,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Z Reminder",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Version 1.0.0 (Phase 1 Foundation) • 100% Offline",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}

@Composable
fun ThemeOptionChip(
    title: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .clip(ChipShape)
            .background(
                if (isSelected) PrimaryViolet
                else MaterialTheme.colorScheme.surfaceVariant
            )
            .clickable { onClick() }
            .padding(vertical = 10.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
            ),
            color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
        )
    }
}
