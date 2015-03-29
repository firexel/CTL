package com.seraph.luigi

/**
 * Luigi
 * Created by seraph on 22.02.2015 23:21.
 */

public open class Buffer<T>(initialValue: T) : ObservableProducer<T>(), Consumer<T> {
    private var value = initialValue

    synchronized public override fun write(value: T) {
        this.value = value
        invokeObserver()
    }

    synchronized public override fun read(): T {
        return value
    }
}