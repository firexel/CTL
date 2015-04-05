package com.seraph.luigi

/**
 * Luigi
 * Created by Alexander Naumov on 05.04.2015.
 */

public trait Consumer<in T> {
    fun ignoreProducer(producer: Producer<T>)
    fun bindProducer(producer: Producer<T>)
    fun requestRead()
}

public trait Producer<out T> {
    fun bindConsumer(consumer: Consumer<T>)
    fun ignoreConsumer(consumer: Consumer<T>)
    fun read():T
}

public abstract class BaseConsumer<T> : Consumer<T> {
    synchronized protected var producer: Producer<T>? = null
        private set

    synchronized override fun bindProducer(producer: Producer<T>) {
        this.producer = producer
    }

    synchronized override fun ignoreProducer(producer: Producer<T>) {
        if (this.producer == producer) {
            this.producer = null
        }
    }
}

fun <T, P:Producer<T>, C:Consumer<T>> P.sinkTo(consumer: C):C {
    this bindConsumer consumer
    consumer bindProducer this
    return consumer
}

public abstract class BaseProducer<T> : Producer<T> {
    synchronized protected var consumer: Consumer<T>? = null
        private set

    synchronized override fun bindConsumer(consumer: Consumer<T>) {
        this.consumer = consumer
    }

    synchronized override fun ignoreConsumer(consumer: Consumer<T>) {
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

    synchronized override fun bindProducer(producer: Producer<I>) {
        this.producer = producer
    }

    synchronized override fun ignoreProducer(producer: Producer<I>) {
        if (this.producer == producer) {
            this.producer = null
        }
    }

    synchronized override fun bindConsumer(consumer: Consumer<O>) {
        this.consumer = consumer
    }

    synchronized override fun ignoreConsumer(consumer: Consumer<O>) {
        if (this.consumer == consumer) {
            this.consumer = null
        }
    }
}

