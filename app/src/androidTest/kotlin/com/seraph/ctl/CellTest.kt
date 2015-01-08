package com.seraph.ctl

import junit.framework.TestCase
import kotlin.test.assertEquals

/**
 * CTL
 * Created by seraph on 08.01.2015 4:06.
 */

public class CellTest : TestCase() {
    private var cell = TestCell();

    override fun setUp() {
        super.setUp()
        cell = TestCell()
    }

    public fun test_cell_shouldHaveAValueProperty() {
        cell.value = "a"
        assertEquals("a", cell.value)
    }

    public fun test_cell_shouldContainAReadOnlyChangeTrigger() {
        cell.trigger
    }
}