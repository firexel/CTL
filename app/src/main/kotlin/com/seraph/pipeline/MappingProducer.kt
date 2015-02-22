package com.seraph.pipeline

/**
 * CTL
 * Created by seraph on 23.02.2015 0:36.
 */

public open class MappingProducer<I, O>(producer: Producer<I>, transformation: (I) -> O) : ObservableProducer<O>() {
    private val wrappedProducer = producer
    private val transformation = transformation
    {
        wrappedProducer.observe { invokeObserver() }
    }

    override fun read(): O {
        return transformation(wrappedProducer.read())
    }
}

public fun <I, O> Producer<I>.map(transformation: (I) -> O): Producer<O> {
    return MappingProducer(this, transformation)
}