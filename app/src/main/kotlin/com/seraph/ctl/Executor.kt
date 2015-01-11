package com.seraph.ctl

import android.os.Looper
import android.os.Handler

/**
 * CTL
 * Created by seraph on 09.01.2015 2:41.
 */

public trait Executor {
    public fun execute(func: () -> Unit)
    public fun cancelAll()
}

public class DegenerateExecutor : Executor {
    override fun execute(func: () -> Unit) {
        // do nothing
    }

    override fun cancelAll() {
        // do nothing
    }
}

public class ImmediateExecutor : Executor {
    override fun execute(func: () -> Unit) {
        func()
    }

    override fun cancelAll() {
        // do nothing
    }
}

public class HandlerExecutor(looper: Looper? = null) : Executor {
    private val handler: Handler = if (looper == null) Handler() else Handler(looper)

    override fun execute(func: () -> Unit) {
        handler.post(func)
    }

    override fun cancelAll() {
        handler.removeCallbacksAndMessages(null)
    }
}