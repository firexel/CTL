package com.seraph.ctl.executor

/**
 * CTL
 * Created by seraph on 09.01.2015 2:41.
 */

public trait Executor {
    public fun execute(func: () -> Unit)
    public fun cancel(func: () -> Unit)
    public fun cancelAll()
}