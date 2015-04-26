package com.seraph.luigi

/**
 * Luigi
 * Created by Alexander Naumov on 05.04.2015.
 */

public trait Consumer<T> {
    fun bindProducer(producer: Producer<T>)
    fun unbindProducer(): Producer<T>?
    fun requestRead()
}

public trait Producer<T> {
    fun bindConsumer(consumer: Consumer<T>)
    fun unbindConsumer(): Consumer<T>?
    fun read():T
}

public abstract class BaseConsumer<T> : Consumer<T> {
    synchronized protected var producer: Producer<T>? = null
        private set

    synchronized override fun bindProducer(producer: Producer<T>) {
        this.producer = producer
    }

    synchronized override fun unbindProducer(): Producer<T>? {
        val producer = this.producer
        this.producer = null
        return producer
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

    synchronized override fun unbindConsumer(): Consumer<T>? {
        val consumer = this.consumer
        this.consumer = null
        return consumer
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

    synchronized override fun unbindProducer(): Producer<I>? {
        val producer = this.producer
        this.producer = null
        return producer
    }

    synchronized override fun bindConsumer(consumer: Consumer<O>) {
        this.consumer = consumer
    }

    synchronized override fun unbindConsumer(): Consumer<O>? {
        val consumer = this.consumer
        this.consumer = null
        return consumer
    }
}

