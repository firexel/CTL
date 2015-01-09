package com.seraph.ctl

import java.util.ArrayList

public open class Trigger<T> : ScopeComponent {

    private var listeners: MutableCollection<TriggerListener<T>> = ArrayList()
    synchronized public var isArmed: Boolean = false;
        private set

    synchronized fun arm(old: T, new: T) {
        isArmed = true
        listeners.forEach { it.onTriggerArmed(this, old, new) }
    }

    synchronized fun disarm() {
        isArmed = false
    }

    synchronized public fun addListener(listener: TriggerListener<T>) {
        this.listeners.add(listener)
    }

    synchronized fun removeListener(listener: TriggerListener<T>) {
        listeners.remove(listener)
    }
}

public class CellChangeTrigger<T>(cell: Cell<T>) : Trigger<T>() {
    private val cell = cell;
    override val precursorComponents: Collection<ScopeComponent>
        get() = listOf(cell)
}

public trait TriggerListener<T> {
    public fun onTriggerArmed(trigger: Trigger<T>, oldValue: T, newValue: T)
}