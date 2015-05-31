package com.seraph.luigi

/**
 * Luigi
 * Created by Alexander Naumov on 12.04.2015.
 */

public class AsyncProducer<T>(
        private val source: Producer<T>,
        private val executor: Executor) : BaseProducer<ProducingState<T>>() {

    private var state: ProducingState<T>? = null
    private var age: Int = 0
    private var dirty = true

    init {
        source sinkTo NotifierConsumer<T>()
    }

    override synchronized fun produce(): ProducingState<T> {
        if (dirty) {
            dirty = false
            state = Reading()
            update()
        }
        return state!!;
    }

    private fun update() {
        val thisUpdateAge = ++age
        executor.cancel()
        executor.exec {
            try {
                setState(thisUpdateAge, Ready(source.produce()))
            } catch(th: Throwable) {
                setState(thisUpdateAge, Error(th))
            }
        }
    }

    private synchronized fun setState(updateAge: Int, state: ProducingState<T>) {
        if (age == updateAge) {
            this.state = state
            consumer?.consume()?.invoke()
        }
    }

    private inner class NotifierConsumer<T> : BaseConsumer<T>() {
        override fun consume(): (() -> Unit)? {
            synchronized(this@AsyncProducer) {
                dirty = true
            }
            return consumer?.consume();
        }
    }
}

public interface ProducingState<T> // it is part of public interface of producer. so must be explicit
public data class Reading<T>() : ProducingState<T>
public data class Ready<T>(val data: T) : ProducingState<T>
public data class Error<T>(val exception: Throwable) : ProducingState<T>

public interface Executor {
    fun cancel()
    fun exec(function: () -> Unit)
}

public fun <T> Producer<T>.observeAt(executor: Executor): Producer<ProducingState<T>> {
    return AsyncProducer(this, executor)
}
