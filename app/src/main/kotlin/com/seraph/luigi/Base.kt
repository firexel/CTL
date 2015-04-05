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

public abstract class BaseProducer<T> : Producer<T> {
    synchronized protected var consumer: Consumer<T>? = null
        private set

    synchronized override fun bindConsumer(consumer: Consumer<T>) {
        this.consumer = consumer
        consumer.bindProducer(this)
    }

    synchronized override fun ignoreConsumer(consumer: Consumer<T>) {
        if (this.consumer == consumer) {
            this.consumer = null
            consumer.ignoreProducer(this)
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
        consumer.bindProducer(this)
    }

    synchronized override fun ignoreConsumer(consumer: Consumer<O>) {
        if (this.consumer == consumer) {
            this.consumer = null
            consumer.ignoreProducer(this)
        }
    }
}

