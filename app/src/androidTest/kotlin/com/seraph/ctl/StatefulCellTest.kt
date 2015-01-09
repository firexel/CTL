package com.seraph.ctl

import junit.framework.TestCase
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.assertFalse

/**
 * CTL
 * Created by seraph on 08.01.2015 4:06.
 */

public class StatefulCellTest : TestCase() {
    private var cell = StatefulCell<String?>("");

    override fun setUp() {
        super.setUp()
        cell = StatefulCell("")
    }

    public fun test_statefullCell_shouldStoreAValue() {
        cell.value = "a"
        assertEquals("a", cell.value)
    }

    public fun test_statefullCell_shouldArmATrigger() {
        assertFalse(cell.trigger.isArmed)
        cell.value = "a"
        assertTrue(cell.trigger.isArmed)
    }

    public fun test_statefullCell_shouldNotArmATrigger_ifSameValueSet() {
        cell.value = null
        assertTrue(cell.trigger.isArmed)
        cell.trigger.disarm()

        cell.value = null
        assertFalse(cell.trigger.isArmed)

        cell.value = "a"
        assertTrue(cell.trigger.isArmed)
        cell.trigger.disarm()

        cell.value = "a"
        assertFalse(cell.trigger.isArmed)

        cell.value = null
        assertTrue(cell.trigger.isArmed)
        cell.trigger.disarm()

        cell.value = null
        assertFalse(cell.trigger.isArmed)
    }
}