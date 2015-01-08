package com.seraph.ctl

import junit.framework.TestCase
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * CTL
 * Created by seraph on 08.01.2015 5:01.
 */
public class TriggerTest : TestCase() {
    public fun test_trigger_shouldBeAbleToNotifyListeners() {
        class TestTriggerListener {
            var notified = false
            fun listen(old: String, new: String) {
                assertEquals("old", old)
                assertEquals("new", new)
                notified = true
            }
        }
        val listeners = listOf(
                TestTriggerListener(),
                TestTriggerListener()
        )
        val trigger = Trigger<String>()
        listeners.forEach { trigger.listen {(old, new) -> it.listen(old, new) } }
        trigger.notify("old", "new")
        listeners.forEach { assertTrue(it.notified) }
    }
}