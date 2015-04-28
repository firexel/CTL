package com.seraph.luigi

/**
 * Luigi
 * Created by Alexander Naumov on 05.04.2015.
 */

public class Buffer<T>(initialValue: T = null) : BaseConsumerProducer<T, T>() {
    synchronized private var value: T = initialValue
    private var dirty = false

    synchronized override fun bindProducer(producer: Producer<T>) {
        super.bindProducer(producer)
        requestRead()
    }

    synchronized override fun read(): T {
        val producer = producer
        if (dirty && producer != null) {
            value = producer.read()
            dirty = false
        }
        return value
    }

    synchronized override fun requestRead(): Boolean {
        dirty = true
        return consumer != null && consumer!!.requestRead()
    }
}
