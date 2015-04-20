package com.seraph.luigi

/**
 * Luigi
 * Created by Alexander Naumov on 21.04.2015.
 */

public fun <O, P : Producer<O>> P.observeRequests(observer: () -> Unit) {
    this sinkTo RequestObserver<O>(observer)
}

public class RequestObserver <T>(val observer: () -> Unit) : BaseConsumer<T>() {
    override fun requestRead() {
        observer.invoke()
    }
}

public fun <O, P : Producer<O>> P.observeData(observer: (O) -> Unit) {
    this sinkTo DataObserver(observer)
}

public class DataObserver <T>(val observer: (T) -> Unit) : BaseConsumer<T>() {
    override fun requestRead() {
        if (producer != null) {
            observer.invoke(producer!!.read())
        }
    }
}