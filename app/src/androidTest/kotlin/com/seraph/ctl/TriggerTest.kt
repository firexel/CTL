package com.seraph.ctl

import junit.framework.TestCase
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.assertFalse

/**
 * CTL
 * Created by seraph on 08.01.2015 5:01.
 */
public class TriggerTest : TestCase() {
    public fun test_trigger_shouldBeAbleToNotifyListeners() {
        val listeners = listOf(
                TestTriggerListener(),
                TestTriggerListener()
        )
        val trigger = Trigger<String>()
        listeners.forEach { trigger.addListener(it) }
        trigger.arm("old", "new")
        listeners.forEach { assertTrue(it.notified) }
    }

    public fun test_trigger_shouldBeAbleToRemoveListeners() {
        val listener = TestTriggerListener()
        val trigger = Trigger<String>()
        trigger.addListener(listener)
        trigger.removeListener(listener)
        trigger.arm("old", "new")
        assertFalse(listener.notified)
    }

    class TestTriggerListener : TriggerListener<String> {
        var notified = false
        override fun onTriggerArmed(trigger: Trigger<String>, oldValue: String, newValue: String) {
            assertEquals("old", oldValue)
            assertEquals("new", newValue)
            notified = true
        }
    }

}