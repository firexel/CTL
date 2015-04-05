package com.seraph.luigi

/**
 * Luigi
 * Created by Alexander Naumov on 05.04.2015.
 */

public trait Consumer<in T> {
    fun ignore(producer: Producer<T>)
    fun observe(producer: Producer<T>)
    fun requestRead()
}

public trait Producer<out T> {
    fun sinkTo(consumer: Consumer<T>)
    fun ignore(consumer: Consumer<T>)
    fun read():T
}

public abstract class BaseConsumer<T> : Consumer<T> {
    synchronized protected var producer: Producer<T>? = null
        private set

    synchronized override fun observe(producer: Producer<T>) {
        this.producer = producer
    }

    synchronized override fun ignore(producer: Producer<T>) {
        if (this.producer == producer) {
            this.producer = null
        }
    }
}

public abstract class BaseProducer<T> : Producer<T> {
    synchronized protected var consumer: Consumer<T>? = null
        private set

    synchronized override fun sinkTo(consumer: Consumer<T>) {
        this.consumer = consumer
    }

    synchronized override fun ignore(consumer: Consumer<T>) {
        if (this.consumer == consumer) {
            this.consumer = null
        }
    }
}

public abstract class BaseConsumerProducer<I, O> : Consumer<I>, Producer<O> {
    synchronized protected var producer: Producer<I>? = null
        private set

    synchronized protected var consumer: Consumer<O>? = null
        private set

    synchronized override fun observe(producer: Producer<I>) {
        this.producer = producer
    }

    synchronized override fun ignore(producer: Producer<I>) {
        if (this.producer == producer) {
            this.producer = null
        }
    }

    synchronized override fun sinkTo(consumer: Consumer<O>) {
        this.consumer = consumer
    }

    synchronized override fun ignore(consumer: Consumer<O>) {
        if (this.consumer == consumer) {
            this.consumer = null
        }
    }
}

