package com.seraph.luigi

/**
 * Luigi
 * Created by Alexander Naumov on 21.04.2015.
 */

public fun <O, P : Producer<O>> P.observeRequests(observer: () -> Unit) {
    this sinkTo RequestObserver<O>(observer)
}

public class RequestObserver <T>(val observer: () -> Unit) : BaseConsumer<T>() {
    override fun requestRead(): Boolean {
        observer.invoke()
        return false
    }
}

public fun <O, P : Producer<O>> P.observeData(observer: (O) -> Unit) {
    this sinkTo DataObserver(observer)
}

public class DataObserver <T>(val observer: (T) -> Unit) : BaseConsumer<T>() {
    override fun requestRead(): Boolean {
        if (producer != null) {
            observer.invoke(producer!!.read())
            return true
        }
        return false
    }
}

public fun <O, P : Producer<O>> P.observeTransfer(observer: (O) -> Unit): Producer<O> {
    return this sinkTo DataTransferObserver(observer)
}

public class DataTransferObserver<O>(val observer: (O) -> Unit) : BaseConsumerProducer<O, O>() {
    override fun requestRead(): Boolean {
        return consumer != null && consumer!!.requestRead()
    }

    override fun read(): O {
        val data = producer!!.read()
        observer.invoke(data)
        return data
    }
}