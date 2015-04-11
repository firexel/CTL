package com.seraph.luigi

import junit.framework.TestCase
import java.util.ArrayList
import java.util.LinkedList
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.fail

/**
 * Luigi
 * Created by Alexander Naumov on 12.04.2015.
 */

public class ObservingProducerTest : TestCase() {
    public fun test_observingProducer_normalFlow() {
        val testProducer = CountingTestProducer<String>()
        val executor = CountingMockExecutor()
        val testConsumer = LoggingTestConsumer<ProducingState>()

        // action
        testProducer.value = "data"
        testProducer observeAt executor sinkTo testConsumer

        // effect
        assertEquals(0, testProducer.readCount)
        assertEquals(1, testConsumer.valuesLog.size())
        assertTrue(testConsumer.valuesLog[0] is ReadingState)

        // action
        executor.execNext()

        // effect
        assertEquals(1, testProducer.readCount)
        assertEquals(2, testConsumer.valuesLog.size())
        assertTrue(testConsumer.valuesLog[0] is ReadingState)
        assertEquals(ReadyState("data"), testConsumer.valuesLog[1])
    }

    public fun test_observingProducer_errorFlow() {
        val testProducer = FaultyProducer()
        val executor = CountingMockExecutor()
        val testConsumer = LoggingTestConsumer<ProducingState>()

        // action
        testProducer observeAt executor sinkTo testConsumer

        // effect
        assertEquals(1, testConsumer.valuesLog.size())
        assertTrue(testConsumer.valuesLog[0] is ReadingState)

        // action
        executor.execNext()

        // effect
        assertEquals(2, testConsumer.valuesLog.size())
        assertTrue(testConsumer.valuesLog[0] is ReadingState)
        assertTrue(testConsumer.valuesLog[1] is ErrorState)
    }
}

private class FaultyProducer : BaseProducer<String>() {
    override fun read(): String {
        throw RuntimeException("Test")
    }
}

private class CountingMockExecutor : Executor {
    public var cancelCallCount: Int = 0
    public var execCallCount: Int = 0
    private val taskQueue = LinkedList<Function0<Unit>>()

    override fun cancel() {
        cancelCallCount++;
    }

    override fun exec(function: () -> Unit) {
        taskQueue.offer(function)
        execCallCount++
    }

    public fun execNext() {
        taskQueue.pop().invoke()
    }
}

private class LoggingTestConsumer<T> : CountingTestConsumer<T>() {
    public val valuesLog: MutableList<T> = ArrayList()

    override fun requestRead() {
        super.requestRead()
        valuesLog.add(value)
    }
}
