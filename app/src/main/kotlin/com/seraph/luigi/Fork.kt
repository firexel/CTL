package com.seraph.luigi

import java.util
import java.util.ArrayList

/**
 * Luigi
 * Created by aleksandr.naumov on 09.05.2015.
 */
public synchronized fun <T> Producer<T>.fork(): Producer<T> {
    val oldConsumer = unbindConsumer()
    if (oldConsumer != null) {
        oldConsumer.unbindProducer()
        val forkConsumer = ForkConsumer<T>()
        this sinkTo forkConsumer
        forkConsumer.producer1 sinkTo oldConsumer
        return forkConsumer.producer2
    } else {
        return this
    }
}

public class ForkConsumer<T> : BaseConsumer<T>() {
    private val port1: Port = Port();
    private val port2: Port = Port();

    public val producer1: Producer<T>
        get() = port1

    public val producer2: Producer<T>
        get() = port2

    override synchronized fun consume(): (() -> Unit)? {
        val callbacks = listOf(port1.innerConsumer?.consume(), port2.innerConsumer?.consume())
        if (callbacks.all { it == null }) {
            return null
        } else {
            return {
                callbacks.forEach { it?.invoke() }
            }
        }
    }

    private inner class Port : BaseProducer<T>() {
        val innerConsumer: Consumer<T>?
            get() = consumer

        override synchronized fun produce(): T {
            if (producer != null) {
                return producer!!.produce()
            } else {
                throw NoDataException("No data due to absence of parent producer")
            }
        }
    }
}