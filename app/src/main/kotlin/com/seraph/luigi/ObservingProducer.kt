package com.seraph.luigi

/**
 * Luigi
 * Created by Alexander Naumov on 12.04.2015.
 */

public class ObservingProducer<T>(
        private val source: Producer<T>,
        private val executor: Executor) : BaseProducer<ProducingState>() {

    private var state: ProducingState? = null
    private var age: Int = 0
    private var dirty = true

    init {
        source sinkTo NotifierConsumer<T>()
    }

    override synchronized fun read(): ProducingState {
        if (dirty) {
            dirty = false
            state = ReadingState()
            update()
        }
        return state!!;
    }

    private fun update() {
        val thisUpdateAge = ++age
        executor.cancel()
        executor.exec {
            try {
                setState(thisUpdateAge, ReadyState(source.read()))
            } catch(th: Throwable) {
                setState(thisUpdateAge, ErrorState(th))
            }
        }
    }

    private synchronized fun setState(updateAge: Int, state: ProducingState) {
        if (age == updateAge) {
            this.state = state
            consumer?.requestRead()
        }
    }

    private inner class NotifierConsumer<T> : BaseConsumer<T>() {
        override fun requestRead() {
            synchronized(this@ObservingProducer) {
                dirty = true
            }
            consumer?.requestRead()
        }
    }
}

public trait ProducingState
public data class ReadingState() : ProducingState
public data class ReadyState<T>(val data: T) : ProducingState
public data class ErrorState(val exception: Throwable) : ProducingState

public trait Executor {
    fun cancel()
    fun exec(function: () -> Unit)
}

public fun <T> Producer<T>.observeAt(executor: Executor): Producer<ProducingState> {
    return ObservingProducer(this, executor)
}
