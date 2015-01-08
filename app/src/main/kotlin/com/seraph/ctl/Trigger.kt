package com.seraph.ctl

import java.util.ArrayList

public open class Trigger<T> {

    private var listeners: MutableCollection<(T, T) -> Unit> = ArrayList()

    public fun listen(listener: (T, T) -> Unit) {
        this.listeners.add(listener)
    }

    fun notify(old: T, new: T) {
        listeners.forEach { it(old, new) }
    }
}