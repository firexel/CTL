package com.seraph.ctl

import kotlin.properties.Delegates

/**
 * CTL
 * Created by seraph on 08.01.2015 4:11.
 */

public abstract class Cell<T> {
    public var value: T
        get() = read()
        set(value) = write(value)

    public val trigger: Trigger<T> by Delegates.lazy {
        createTrigger()
    };

    protected abstract fun read(): T
    protected abstract fun write(newValue: T)
    protected open fun createTrigger(): Trigger<T> = Trigger()
}