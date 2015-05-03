package com.seraph.luigi

/**
 * Luigi
 * Created by Alexander Naumov on 04.05.2015.
 */
public fun <T> Producer<T>.filter(predicate: (T) -> Boolean): Producer<T> {
    return this sinkTo FilterConsumerProducer(predicate)
}

public class FilterConsumerProducer<T>(
        private val predicate: (T) -> Boolean) : BaseConsumerProducer<T, T>() {

    private var acceptedValue: T = null
    private var valueIsAccepted = false

    override synchronized fun consume(): (() -> Unit)? {
        val consumeCallback = consumer?.consume()
        if (consumeCallback != null && producer != null) {
            return {
                if (update()) {
                    consumeCallback.invoke()
                    reset()
                }
            }
        }
        return null
    }

    override synchronized fun produce(): T {
        if (valueIsAccepted || update()) {
            val value = acceptedValue
            reset()
            return value
        }
        throw NoDataException("No data for " + consumer)
    }

    private fun update(): Boolean {
        if (producer != null) {
            val product = producer!!.produce()
            if (predicate(product)) {
                acceptedValue = product
                valueIsAccepted = true
                return true
            }
        }
        return false
    }

    private fun reset() {
        valueIsAccepted = false
        acceptedValue = null
    }
}

public class NoDataException(message: String) : RuntimeException(message)