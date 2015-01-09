package com.seraph.ctl

import junit.framework.TestCase
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * CTL
 * Created by seraph on 10.01.2015 0:15.
 */
public class OrTriggerTest : TestCase() {

    public fun test_orTrigger_shouldArm_ifAnyOfInternalTriggersArmed() {
        val trigger1 = Trigger<String>()
        val trigger2 = Trigger<Int>()
        val orTrigger = OrTrigger<Any?>(trigger1, trigger2)
        assertFalse(orTrigger.isArmed)
        trigger1.arm("", "")
        assertTrue(orTrigger.isArmed)
        trigger1.disarm()
        assertFalse(orTrigger.isArmed)
        trigger2.arm(0, 0)
        assertTrue(orTrigger.isArmed)
        trigger1.arm("", "")
        assertTrue(orTrigger.isArmed)
    }

    public fun test_orTrigger_disarmCausesInternalTriggersDisarm() {
        val trigger1 = Trigger<String>()
        val trigger2 = Trigger<Int>()
        val orTrigger = OrTrigger<Any?>(trigger1, trigger2)
        trigger1.arm("", "")
        trigger2.arm(0, 0)
        assertTrue(trigger1.isArmed && trigger2.isArmed)
        orTrigger.disarm()
        assertFalse(trigger1.isArmed || trigger2.isArmed)
    }
}