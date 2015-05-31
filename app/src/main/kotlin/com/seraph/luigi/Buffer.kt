package com.seraph.luigi

/**
 * Luigi
 * Created by Alexander Naumov on 05.04.2015.
 */

public fun <T> Producer<T>.buffer(defaultValue: T): Producer<T> {
    return this sinkTo Buffer(defaultValue)
}

public class Buffer<T> : BaseConsumerProducer<T, T> {
    synchronized private var value: T
    private var initialized: Boolean
    private var dirty = false

    constructor() {
        initialized = false
        value = null
    }

    constructor(initialValue: T) {
        initialized = true
        value = initialValue
    }

    synchronized override fun bindProducer(producer: Producer<T>) {
        super.bindProducer(producer)
        consume()?.invoke()
    }

    synchronized override fun produce(): T {
        val producer = producer
        if ((dirty || !initialized) && producer != null) {
            try {
                value = producer.produce()
                dirty = false
                initialized = true
            } catch(ex: NoDataException) {
                // ignore, we are already set dirty to true
            }
        }
        if (!initialized) {
            throw NoDataException("${this} is not initialized")
        }
        return value
    }

    synchronized override fun consume(): (() -> Unit)? {
        dirty = true
        return consumer?.consume()
    }
}
