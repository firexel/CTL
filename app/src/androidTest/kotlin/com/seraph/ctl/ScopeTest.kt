package com.seraph.ctl

import junit.framework.TestCase
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.fail
import java.util.ArrayList

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

    public fun test_scope_shouldDetectCyclicDependencies() {
        val cell1 = StatefulCell("")
        val cell2 = StatefulCell("")
        scope.link(cell1).with(cell2)
        scope.link(cell2).with(cell1)
        try {
            scope.build()
            fail("Exception not thrown")
        } catch (ex: BuildException) {
            // ignore
        }
    }

    public fun test_scope_shouldBeAbleToConnect2CellsUsingRule() {
        val src1 = StatefulCell(0)
        val dst = StatefulCell("")
        scope.link(dst).with(src1) { it.toString() }
        scope.build()
        src1.value = 7
        assertEquals("7", dst.value)
    }

    public fun test_scope_shouldBeAbleToConnect3CellsUsingRule() {
        val src1 = StatefulCell("")
        val src2 = StatefulCell("")
        val dst = StatefulCell("")
        scope.link(dst).with(src1, src2) {(s1, s2) -> s1 + s2 }
        scope.build()
        src1.value = "a"
        src2.value = "b"
        assertEquals("ab", dst.value)
    }

    public fun test_scope_shouldBeAbleToConnect4CellsUsingRule() {
        val src1 = StatefulCell("")
        val src2 = StatefulCell("")
        val src3 = StatefulCell("")
        val dst = StatefulCell("")
        scope.link(dst).with(src1, src2, src3) {(s1, s2, s3) -> s1 + s2 + s3 }
        scope.build()
        src1.value = "a"
        src2.value = "b"
        src3.value = "c"
        assertEquals("abc", dst.value)
    }

    public fun test_scope_shouldBeAbleToConnect5CellsUsingRule() {
        val src1 = StatefulCell("")
        val src2 = StatefulCell("")
        val src3 = StatefulCell("")
        val src4 = StatefulCell("")
        val dst = StatefulCell("")
        scope.link(dst).with(src1, src2, src3, src4) {(s1, s2, s3, s4) -> s1 + s2 + s3 + s4 }
        scope.build()
        src1.value = "a"
        src2.value = "b"
        src3.value = "c"
        src4.value = "d"
        assertEquals("abcd", dst.value)
    }

    public fun test_scope_shouldBeAbleToConnect6CellsUsingRule() {
        val src1 = StatefulCell("")
        val src2 = StatefulCell("")
        val src3 = StatefulCell("")
        val src4 = StatefulCell("")
        val src5 = StatefulCell("")
        val dst = StatefulCell("")
        scope.link(dst).with(src1, src2, src3, src4, src5) {(s1, s2, s3, s4, s5) -> s1 + s2 + s3 + s4 + s5 }
        scope.build()
        src1.value = "a"
        src2.value = "b"
        src3.value = "c"
        src4.value = "d"
        src5.value = "e"
        assertEquals("abcde", dst.value)
    }

    public fun test_scope_shouldCancelScheduledUpdatesIfNewUpdateNeeded() {
        class TestAsyncExecutor : Executor {
            var pendingExecutions: MutableCollection<() -> Unit> = ArrayList()

            override fun execute(func: () -> Unit) {
                pendingExecutions.add(func)
            }

            override fun cancelAll() = pendingExecutions.clear()
        }

        val src = StatefulCell("")
        val dst = StatefulCell("")
        val executor = TestAsyncExecutor()
        scope = Scope(executor)
        scope.link(dst).with(src)
        scope.build()
        src.value = "a"
        src.value = "b"
        src.value = "c"
        assertEquals(1, executor.pendingExecutions.size())
        executor.pendingExecutions.forEach { it() }
        assertEquals("c", dst.value)
    }

    public fun test_scope_shouldBeAbleToUnlinkCell() {
        val src = StatefulCell("");
        val dst1 = StatefulCell("");
        val dst2 = StatefulCell("");
        scope.link(dst1).with(src)
        scope.link(dst2).with(src)
        scope.build()
        src.value = "a"
        assertEquals("a", dst1.value)
        assertEquals("a", dst2.value)
        scope.unlink(dst1)
        scope.build()
        src.value = "b"
        assertEquals("a", dst1.value)
        assertEquals("b", dst2.value)
    }
}

