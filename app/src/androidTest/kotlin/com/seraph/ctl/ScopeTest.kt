package com.seraph.ctl

import junit.framework.TestCase
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.fail
import com.seraph.ctl.DegenerateExecutor

/**
 * CTL
 * Created by seraph on 09.01.2015 2:51.
 */
public class ScopeTest : TestCase() {

    private var scope: Scope = Scope()

    override fun setUp() {
        scope = Scope()
    }

    public fun test_scope_shouldBeAbleToLinkCellsDirectly() {
        val srcCell = StatefulCell("")
        val dstCell = StatefulCell("")
        scope.link(dstCell).with(srcCell)
        scope.build()
        srcCell.value = "a"
        assertEquals("a", dstCell.value)
        assertFalse(srcCell.trigger.isArmed)
    }

    public fun test_scope_shouldThrowBuildException_ifLinkConstructionNotFinished() {
        scope.link(StatefulCell(""))
        try {
            scope.build()
            fail("Exception not thrown")
        } catch (ex: BuildException) {
            // pass
        }
    }

    public fun test_scope_shouldUpdateCellsInTopologicalOrderInOnePass() {
        val cells = Array(3) {(i) -> StatefulCell("") }
        scope = Scope(DegenerateExecutor())
        scope.link(cells[2]).with(cells[1])
        scope.link(cells[1]).with(cells[0])
        scope.build()
        cells[0].value = "a"
        scope.update()
        assertEquals("a", cells[1].value)
        assertEquals("a", cells[2].value)
    }
}

