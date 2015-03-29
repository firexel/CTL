package com.seraph.pipeline

/**
 * CTL
 * Created by seraph on 02.03.2015 0:37.
 */
public open class Connector<T>(producer: Producer<T>, consumer: Consumer<T>) {

    init {
        performTransfer(consumer, producer)
        producer.observe { performTransfer(consumer, producer) }
    }

    protected open fun <T> performTransfer(consumer: Consumer<T>, producer: Producer<T>) {
        try {
            consumer.write(producer.read())
        } catch (e: Producer.NoDataException) {
            // do nothing
        }
    }
}

public fun <T> Producer<T>.sinkTo(consumer:Consumer<T>):Connector<T> {
    return Connector(this, consumer)
}