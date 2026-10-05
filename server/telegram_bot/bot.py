#!/usr/bin/env python3
"""
Z Reminder Telegram Bot Bridge
Runs on Proxmox Server (CT 117 or Docker host VM 100).
Allows submitting reminders from Telegram directly into Z Reminder format.
"""

import os
import sys
import json
import time
import logging
from datetime import datetime, timedelta
import telebot

logging.basicConfig(
    format="%(asctime)s - %(name)s - %(levelname)s - %(message)s",
    level=logging.INFO
)
logger = logging.getLogger("ZReminderBot")

BOT_TOKEN = os.getenv("Z_REMINDER_BOT_TOKEN", "YOUR_TELEGRAM_BOT_TOKEN_HERE")
ALLOWED_USER_ID = os.getenv("ALLOWED_TELEGRAM_USER_ID", "") # Tuhin's Telegram Chat ID

bot = telebot.TeleBot(BOT_TOKEN, parse_mode="HTML")

REMINDERS_FILE = os.path.expanduser("~/z_reminders_feed.json")

def load_reminders():
    if os.path.exists(REMINDERS_FILE):
        try:
            with open(REMINDERS_FILE, "r", encoding="utf-8") as f:
                return json.load(f)
        except Exception:
            return []
    return []

def save_reminders(reminders):
    with open(REMINDERS_FILE, "w", encoding="utf-8") as f:
        json.dump(reminders, f, indent=2, ensure_ascii=False)

def is_authorized(message):
    if not ALLOWED_USER_ID:
        return True
    return str(message.from_user.id) == str(ALLOWED_USER_ID)

@bot.message_handler(commands=["start", "help"])
def handle_start(message):
    if not is_authorized(message):
        return
    text = (
        "<b>⏰ Z Reminder Bot Bridge</b>\n\n"
        "Commands:\n"
        "• <code>/add &lt;text&gt; [in X mins / at HH:MM]</code> - Create reminder\n"
        "• <code>/list</code> - Show pending server reminders\n"
        "• <code>/export</code> - Get JSON file for Z Reminder Android import\n"
        "• <code>/clear</code> - Clear synced reminders\n\n"
        "Or simply forward any message here to turn it into a task!"
    )
    bot.reply_to(message, text)

@bot.message_handler(commands=["add"])
def handle_add(message):
    if not is_authorized(message):
        return
    raw_text = message.text[len("/add"):].strip()
    if not raw_text:
        bot.reply_to(message, "Usage: <code>/add Submit tax invoice in 2 hours</code>")
        return

    due_epoch = int((time.time() + 3600) * 1000) # Default +1h
    title = raw_text

    # Parse simple relative timings
    lower = raw_text.lower()
    if " in " in lower:
        parts = lower.split(" in ")
        title = raw_text[:lower.rfind(" in ")].strip()
        time_spec = parts[-1].strip()
        if "min" in time_spec:
            mins = int("".join(filter(str.isdigit, time_spec)) or 10)
            due_epoch = int((time.time() + mins * 60) * 1000)
        elif "hour" in time_spec or "hr" in time_spec:
            hrs = int("".join(filter(str.isdigit, time_spec)) or 1)
            due_epoch = int((time.time() + hrs * 3600) * 1000)

    reminders = load_reminders()
    new_rem = {
        "title": title,
        "notes": f"Submitted via Telegram at {datetime.now().strftime('%Y-%m-%d %H:%M')}",
        "dueAt": due_epoch,
        "priority": "NORMAL",
        "alertStyle": "FULL_SCREEN",
        "backgroundId": "lavender_dream"
    }
    reminders.append(new_rem)
    save_reminders(reminders)

    due_dt = datetime.fromtimestamp(due_epoch / 1000.0).strftime('%I:%M %p')
    bot.reply_to(message, f"✓ Added: <b>{title}</b>\n⏰ Due at: <b>{due_dt}</b>")

@bot.message_handler(commands=["list"])
def handle_list(message):
    if not is_authorized(message):
        return
    reminders = load_reminders()
    if not reminders:
        bot.reply_to(message, "No pending reminders in server feed.")
        return

    lines = ["<b>📋 Pending Server Reminders:</b>"]
    for i, r in enumerate(reminders, 1):
        dt = datetime.fromtimestamp(r["dueAt"] / 1000.0).strftime('%b %d %I:%M %p')
        lines.append(f"{i}. <b>{r['title']}</b> ({dt})")
    bot.reply_to(message, "\n".join(lines))

@bot.message_handler(commands=["export"])
def handle_export(message):
    if not is_authorized(message):
        return
    reminders = load_reminders()
    if not reminders:
        bot.reply_to(message, "No reminders to export.")
        return

    filepath = "/tmp/z_reminders.json"
    with open(filepath, "w", encoding="utf-8") as f:
        json.dump(reminders, f, indent=2, ensure_ascii=False)

    with open(filepath, "rb") as doc:
        bot.send_document(
            message.chat.id,
            doc,
            caption="Here is your Z Reminder backup file. You can import it directly into Z Reminder app Settings → Backup & Restore."
        )

if __name__ == "__main__":
    logger.info("Starting Z Reminder Telegram Bot...")
    bot.infinity_polling()
