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

public class AsyncProducerTest : TestCase() {
    public fun test_observingProducer_normalFlow() {
        val testProducer = CountingTestProducer<String>()
        val executor = CountingMockExecutor()
        val testConsumer = LoggingTestConsumer<ProducingState<String>>()

        // action
        testProducer.value = "data"
        testProducer observeAt executor sinkTo testConsumer

        // effect
        assertEquals(0, testProducer.produceCount)
        assertEquals(1, testConsumer.statesLog.size())
        assertTrue(testConsumer.statesLog[0] is Reading)

        // action
        executor.execNext()

        // effect
        assertEquals(1, testProducer.produceCount)
        assertEquals(2, testConsumer.statesLog.size())
        assertTrue(testConsumer.statesLog[0] is Reading)
        assertEquals(Ready("data"), testConsumer.statesLog[1])

        // action
        testProducer.value = "data2"
        testProducer.emitReadRequest()

        // effect
        assertEquals(1, testProducer.produceCount)
        assertEquals(3, testConsumer.statesLog.size())
        assertTrue(testConsumer.statesLog[0] is Reading)
        assertEquals(Ready("data"), testConsumer.statesLog[1])
        assertTrue(testConsumer.statesLog[2] is Reading)

        // action
        executor.execNext() // this should cause to read() being performed

        // effect
        assertEquals(2, testProducer.produceCount)
        assertEquals(4, testConsumer.statesLog.size())
        assertTrue(testConsumer.statesLog[0] is Reading)
        assertEquals(Ready("data"), testConsumer.statesLog[1])
        assertTrue(testConsumer.statesLog[2] is Reading)
        assertEquals(Ready("data2"), testConsumer.statesLog[3])
    }

    public fun test_observingProducer_errorFlow() {
        val testProducer = FaultyProducer()
        val executor = CountingMockExecutor()
        val testConsumer = LoggingTestConsumer<ProducingState<String>>()

        // action
        testProducer observeAt executor sinkTo testConsumer

        // effect
        assertEquals(1, testConsumer.statesLog.size())
        assertTrue(testConsumer.statesLog[0] is Reading)

        // action
        executor.execNext()

        // effect
        assertEquals(2, testConsumer.statesLog.size())
        assertTrue(testConsumer.statesLog[0] is Reading)
        assertTrue(testConsumer.statesLog[1] is Error)
    }
}

private class FaultyProducer : BaseProducer<String>() {
    override fun produce(): String {
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
    public val statesLog: MutableList<T> = ArrayList()

    override fun consume(): (() -> Unit)? {
        val function = super.consume()
        return {
            function?.invoke()
            statesLog.add(value)
        }
    }
}
