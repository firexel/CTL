package com.seraph.pipeline

import com.seraph.pipeline.Producer.NoDataException

/**
 * CTL
 * Created by seraph on 23.02.2015 0:09.
 */

public open class EventProducer<T> : ObservableProducer<T>() {
    var armed = false
    var storedEvent:T = null

    synchronized public open fun fire(event:T) {
        armed = true
        storedEvent = event
        invokeObserver()
    }

    synchronized public override fun read(): T {
        if(armed) {
            val event = storedEvent
            armed = false
            storedEvent = null
            return event
        } else {
            throw NoDataException();
        }
    }
}
