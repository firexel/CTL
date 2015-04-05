package com.seraph.luigi

/**
 * Luigi
 * Created by Alexander Naumov on 05.04.2015.
 */

public class ConstantProducer<T>(value:T) : BaseProducer<T>() {
    private final val value = value

    override fun read(): T {
        return value
    }
}

fun <T, C:Consumer<T>> T.bindTo(consumer: C):C {
    ConstantProducer(this) bindConsumer consumer
    return consumer
}
