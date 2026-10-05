package com.z.reminder.data

import com.z.reminder.data.model.Reminder
import org.json.JSONArray
import org.json.JSONObject
import org.junit.Assert.assertEquals
import org.junit.Test

class ReminderJsonTest {

    @Test
    fun `test export and import JSON serialization`() {
        val reminders = listOf(
            Reminder(
                id = 1,
                title = "Submit VAT Invoice",
                notes = "Check file copy",
                dueAt = 1770000000000L,
                priority = "HIGH",
                repeatUnit = "MONTH",
                repeatInterval = 1
            ),
            Reminder(
                id = 2,
                title = "Office Team Sync",
                notes = "Room 302",
                dueAt = 1770005000000L
            )
        )

        // Serialize
        val array = JSONArray()
        for (r in reminders) {
            val obj = JSONObject().apply {
                put("title", r.title)
                put("notes", r.notes)
                put("dueAt", r.dueAt)
                put("priority", r.priority)
                put("repeatUnit", r.repeatUnit)
                put("repeatInterval", r.repeatInterval)
            }
            array.put(obj)
        }
        val jsonStr = array.toString()

        // Deserialize
        val parsedArray = JSONArray(jsonStr)
        assertEquals(2, parsedArray.length())
        val first = parsedArray.getJSONObject(0)
        assertEquals("Submit VAT Invoice", first.getString("title"))
        assertEquals("Check file copy", first.getString("notes"))
        assertEquals("HIGH", first.getString("priority"))
        assertEquals("MONTH", first.getString("repeatUnit"))
        assertEquals(1, first.getInt("repeatInterval"))
    }
}
