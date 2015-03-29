package com.seraph.luigi

import com.seraph.luigi.Producer.NoDataException

/**
 * Luigi
 * Created by seraph on 23.02.2015 0:09.
 */

public open class EventProducer<T> : ObservableProducer<T>() {
    public var armed: Boolean = false
        private set

    var storedEvent: T = null

    synchronized public open fun fire(event: T) {
        armed = true
        storedEvent = event
        if (!invokeObserver()) {
            read()
        }
    }

    synchronized public override fun read(): T {
        if (armed) {
            val event = storedEvent
            armed = false
            storedEvent = null
            return event
        } else {
            throw NoDataException();
        }
    }
}
