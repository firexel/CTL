package com.seraph.ctl.executor

/**
 * CTL
 * Created by seraph on 09.01.2015 2:54.
 */
public class ImmediateExecutor : Executor {
    override fun execute(func: () -> Unit) {
        func()
    }

    override fun cancel(func: () -> Unit) {
        // do nothing
    }

    override fun cancelAll() {
        // do nothing
    }
}
