package com.seraph.ctl.executor

import android.os.Looper
import android.os.Handler

public class HandlerExecutor(looper: Looper? = null) : Executor {
    private val handler: Handler = if (looper == null) Handler() else Handler(looper)

    override fun execute(func: () -> Unit) {
        handler.post(func)
    }

    override fun cancel(func: () -> Unit) {
        handler.removeCallbacks(func)
    }

    override fun cancelAll() {
        handler.removeCallbacksAndMessages(null)
    }
}