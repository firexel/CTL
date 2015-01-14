package com.seraph.ctl

import kotlin.test.assertTrue

/**
 * CTL
 * Created by seraph on 14.01.2015 9:57.
 */
public class ExecutorTest {
    public fun test_immediateExecutor_shouldExecuteTaskInSameCall() {
        val executor = ImmediateExecutor()
        var executed = false
        executor.execute { executed = true }
        assertTrue(executed)
    }
}
