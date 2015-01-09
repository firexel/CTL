package com.seraph.ctl

import junit.framework.TestCase
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.fail

/**
 * CTL
 * Created by seraph on 09.01.2015 2:51.
 */
public class ScopeTest : TestCase() {
    public fun test_scope_shouldBeAbleToLinkCellsDirectly() {
        val scope = Scope()
        val srcCell = StatefulCell("")
        val dstCell = StatefulCell("")
        scope.link(dstCell).with(srcCell)
        scope.build()
        srcCell.value = "a"
        assertEquals("a", dstCell.value)
        assertFalse(srcCell.trigger.isArmed)
    }

    public fun test_scope_shouldThrowBuildException_ifLinkConstructionNotFinished() {
        val scope = Scope()
        scope.link(StatefulCell(""))
        try {
            scope.build()
            fail("Exception not thrown")
        } catch (ex: BuildException) {
            // pass
        }
    }
}

