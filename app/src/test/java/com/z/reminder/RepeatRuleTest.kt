package com.z.reminder

import com.z.reminder.data.model.RepeatMode
import com.z.reminder.data.model.RepeatRule
import com.z.reminder.data.model.RepeatUnit
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class RepeatRuleTest {

    @Test
    fun testWeeklyBitmaskCreation() {
        // Monday (1) and Thursday (4)
        val rule = RepeatRule.createWeekly(listOf(1, 4))
        assertEquals(RepeatUnit.WEEK, rule.unit)
        assertTrue(rule.isDaySelected(1))
        assertTrue(rule.isDaySelected(4))
        assertFalse(rule.isDaySelected(2))
        assertFalse(rule.isDaySelected(3))
        assertFalse(rule.isDaySelected(5))
    }

    @Test
    fun testDefaultRepeatRuleParameters() {
        val rule = RepeatRule(unit = RepeatUnit.DAY, interval = 2)
        assertEquals(2, rule.interval)
        assertEquals(RepeatUnit.DAY, rule.unit)
        assertEquals(RepeatMode.FROM_DUE_TIME, rule.mode)
    }
}
