package com.seraph.luigi

/**
 * Luigi
 * Created by Alexander Naumov on 05.04.2015.
 */

public fun <T> Producer<T>.buffer(defaultValue: T): Producer<T> {
    return this sinkTo Buffer(defaultValue)
}

public class Buffer<T>(initialValue: T = null) : BaseConsumerProducer<T, T>() {
    synchronized private var value: T = initialValue
    private var dirty = false

    synchronized override fun bindProducer(producer: Producer<T>) {
        super.bindProducer(producer)
        consume()?.invoke()
    }

    synchronized override fun produce(): T {
        val producer = producer
        if (dirty && producer != null) {
            try {
                value = producer.produce()
                dirty = false
            } catch(ex: NoDataException) {
                // ignore, we are already set dirty to true
            }
        }
        return value
    }

    synchronized override fun consume(): (() -> Unit)? {
        dirty = true
        return consumer?.consume()
    }
}
