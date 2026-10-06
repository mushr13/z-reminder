# Mission: Senior Android Software Engineering Audit & Feature Verification

## Target Repository:
```bash
git clone https://github.com/mushr13/z-reminder.git
cd z-reminder
./gradlew testDebugUnitTest
```

## Role & Goal:
Act as a Principal Android Software Engineer. Conduct an exhaustive, code-level verification and audit of the entire **Z-Reminder** codebase. 
Your objective is to verify that **every single claimed feature and capability below actually exists, is fully wired up in the code, and is implemented to production Android standards**.

For each feature below, perform a strict code check:
1. **Implementation Status**: Is it fully implemented, partially implemented, or missing/mocked?
2. **File & Symbol Location**: What file, class, and method powers it?
3. **Engineering Integrity**: Are there race conditions, lifecycle bugs, audio/memory leaks, or Android 14–16 OEM background restrictions (Honor MagicOS / Samsung / Xiaomi Doze)?
4. **Pass/Fail Verdict**: State clearly if the feature passes verification.

---

## Complete Feature-by-Feature Engineering Audit Checklist:

### 1. Reminder Creation & Customization (Add / Edit Sheet)
- **Title & Notes**: Verify Room persistence, character handling, and keyboard dismiss behavior in `AddEditReminderSheet.kt`.
- **Date & Time Picker**: Verify native Android `DatePickerDialog` and `TimePickerDialog` integration.
- **Quick Time Presets**: Check calculation logic for `Morning` (9 AM), `Lunch` (1 PM), `Evening` (6 PM), `Night` (10 PM), and `+1 Hour` in `QuickPreset.kt` & `ReminderRepository.kt`.
- **Repeat Engine**:
  - `Daily`, `Monthly` repeat calculations in `RepeatEngine.kt`.
  - `Weekly` repeat: Check the bitmask implementation (`repeatWeekdaysMask`) for arbitrary day selection (e.g. Mon/Wed/Fri).
  - `Custom Interval`: Check hours and minutes calculation (15m, 30m, 45m, 1h, 2h, etc.).
  - Repeat Mode: Check `FROM_DUE_TIME` vs `FROM_COMPLETION_TIME` logic.
  - "Clear Repeat": Verify reset to one-off reminder.
- **Priority Toggle**: Normal vs Urgent flag toggle.
- **Sticky Pinning**: Verify `isPinned = true` is the default on all new reminders.
- **Visual Design**: Verify the glowing green Save button styling.

### 2. Live Firing Alerts & Lockscreen Awakening
- **Exact Alarm Clock**: Verify `AlarmScheduler.kt` uses `AlarmManager.setAlarmClock()` with `showIntent`.
- **Hardware Wake-Lock**: Inspect `AlarmReceiver.kt` for `PowerManager.WakeLock` (`ACQUIRE_CAUSES_WAKEUP or FULL_WAKE_LOCK`). Ensure it acquires instantly upon alarm trigger and has a timeout release (15s) to prevent battery drain.
- **Keyguard Bypass**: Verify `KeyguardManager.requestDismissKeyguard()` and `setShowWhenLocked(true)` in `FullScreenAlertActivity.kt`.
- **Heads-Up Banner**: Verify `NotificationHelper.kt` creates `z_reminder_alarm_heads_up_v2` channel with `IMPORTANCE_HIGH`, `lockscreenVisibility = VISIBILITY_PUBLIC`, `bypassDnd = true`, `PRIORITY_MAX`, `CATEGORY_ALARM`, and attached action buttons (`✓ Complete` and `💤 Snooze 15m`).
- **Full-Screen Activity**: Inspect `FullScreenAlertActivity.kt` for live ticking clock, audio playback (`AudioAttributes.USAGE_ALARM`), vibration waveform, and safe resource release (`MediaPlayer.release()`, `Vibrator.cancel()`) on Complete, Snooze, Dismiss, or `onDestroy()`.
- **Nagging Engine**: Verify that Dismiss silences the immediate ring while keeping the reminder scheduled to re-nag at the chosen interval (15, 30, 45, 60 min).
- **Snooze Counter**: Check `snoozeCount` incrementing and red badge styling when snoozed 3+ times.

### 3. Telegram Backup Alert Pipeline
- **Automated Backup Scheduling**: Verify in `AlarmScheduler.kt` that the Telegram alert is pre-scheduled via `setAlarmClock` to fire exactly 1 minute after due time.
- **HTML Formatting**: Verify `TelegramNotifier.kt` escapes `<` `>` `&` and uses `parse_mode = "HTML"`.
- **Network Resilience**: Confirm background HTTP dispatch handles timeouts, offline states, and server errors without crashing the app.
- **Settings Screen Wiring**: Verify Bot Token, Admin Chat ID, and the "Send Test" button functionality in `SettingsViewModel.kt`.

### 4. Wi-Fi Presence Tracking (Home vs. Office)
- **SSID Scanning**: Verify `OfficePresenceMonitor.kt` safely queries Wi-Fi SSID using `NetworkCapabilities` / `WifiInfo` / `WifiManager` without throwing missing permission crashes.
- **Presence Separation**: Verify that connecting to Home Wi-Fi immediately cancels any office check-in state so office reminders NEVER trigger at home.
- **Unknown SSID Handling**: Confirm that unreadable or `<unknown ssid>` networks are safely ignored and never falsely treated as office or home.
- **1-Tap Setup**: Verify "Set as Home" and "Set as Office" buttons and persistence in `SettingsScreen.kt`.

### 5. Today Dashboard & Navigation
- **Header**: Dynamic greeting ("Good morning", etc.) and live date calculation.
- **Search**: Real-time title search filtering in `TodayScreen.kt`.
- **Collapsible Week Strip**: 7-day mini calendar component (`WeekStrip.kt`) and day filtering.
- **Overdue Section**: Overdue card styling, count badge, and sorting.
- **Reminder Cards**: 1-tap checkbox completion, priority red dot, repeat/place chips, and 3-dot menu (`Edit`, `Snooze 10m`, `Snooze 1h`, `Duplicate`, `Delete`).
- **Permission Health Banner**: Automatic warning card when required permissions are missing, with direct deep-links to grant them.

### 6. Upcoming Reminders Screen
- Chronological date groupings in `UpcomingScreen.kt` and `UpcomingViewModel.kt`.
- Item count badges and full context menu actions.

### 7. Completed Reminders & History Screen
- Grouped list of finished reminders in `CompletedScreen.kt`.
- "Restore" (undo completion) and "Permanent Delete" operations.

### 8. Quick Add & Android Quick Settings Tile
- Pull-down tile service (`QuickAddTileService.kt`) declared in `AndroidManifest.xml`.
- Floating quick-add popup dialog (`QuickAddDialogActivity.kt`) with quick presets and instant save.

### 9. Android Home Screen Widget (Today Widget)
- `TodayAppWidgetProvider.kt` implementation.
- Layout verification in `widget_today.xml`: Ensure all XML elements are valid `RemoteViews` tags (e.g. `FrameLayout` instead of forbidden raw `<View>`).
- Auto-sync updates on database changes.

### 10. Settings & Permission Center
- Theme mode switching (System / Light / Dark).
- Nag interval selector (15, 30, 45, 60 min).
- Simulation tools: "Test Full-Screen Alert Now" and "1-Min Lockscreen Test".
- Permission checklist in `SettingsScreen.kt`: Exact Alarms, Notifications, System Alert Window, Battery Optimization whitelist.

---

## Final Deliverables Expected:
1. **Feature Verification Matrix**: Table listing all 10 feature areas with Status (PASS / FAIL / PARTIAL), File Path, and Notes.
2. **Unit Test & Build Results**: Output of `./gradlew testDebugUnitTest` and compile health.
3. **Identified Issues or Code Smells**: Any edge cases, thread safety concerns, memory leaks, or missing pieces discovered.
4. **Recommended Fixes**: Provide exact code patches for any issues found.
