package com.seraph.ctl

import kotlin.properties.Delegates

/**
 * CTL
 * Created by seraph on 08.01.2015 4:11.
 */

public abstract class Cell<T> : ScopeComponent {
    public var value: T
        get() = read()
        set(value) = write(value)

    public val trigger: CellChangeTrigger<T> by Delegates.lazy {
        createTrigger()
    };

    protected open fun notifyChanged(oldValue: T, newValue: T): Unit = trigger.arm(oldValue, newValue)
    protected abstract fun read(): T
    protected abstract fun write(newValue: T)
    protected open fun createTrigger(): CellChangeTrigger<T> = CellChangeTrigger(this)

    override val affectedComponents: Collection<ScopeComponent>
        get() = listOf(trigger)
}

public class StatefulCell<T>(default: T) : Cell<T>() {
    var storedValue: T = default;

    synchronized override fun write(newValue: T) {
        if (shouldAssignNewValue(newValue)) {
            val oldValue = storedValue
            storedValue = newValue
            notifyChanged(oldValue, newValue)
        }
    }

    private fun shouldAssignNewValue(newValue: T): Boolean {
        val translateFromNull = storedValue == null && newValue != null
        val translateToNull = storedValue != null && newValue == null
        val notEquals = storedValue != null && newValue != null && !(storedValue equals newValue)
        return translateFromNull || translateToNull || notEquals
    }

    synchronized override fun read(): T = storedValue
}