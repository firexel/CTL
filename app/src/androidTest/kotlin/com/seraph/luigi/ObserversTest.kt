package com.seraph.luigi

import junit.framework.TestCase
import kotlin.test.assertEquals

/**
 * Luigi
 * Created by Alexander Naumov on 21.04.2015.
 */

public class ObserversTest : TestCase() {
    public fun test_requestObserver_new() {
        val producer = CountingTestProducer<String>()
        assertEquals(0, producer.produceCount)

        var requestObservations = 0
        producer observeRequests {
            requestObservations++
        }

        assertEquals(0, producer.produceCount)
        assertEquals(0, requestObservations)

        repeat(3) { producer.emitReadRequest() }

        assertEquals(0, producer.produceCount)
        assertEquals(3, requestObservations)
    }

    public fun test_dataObserver_new() {
        val producer = CountingTestProducer<String>()
        assertEquals(0, producer.produceCount)

        var observedData: String = ""
        producer observeData { data: String ->
            observedData = data
        }

        listOf("a", "b", "c").forEach {
            producer.value = it
            producer.emitReadRequest()
            assertEquals(observedData, it)
        }

        assertEquals(3, producer.produceCount)
    }

    public fun test_dataTransferObserver_new() {
        val producer = CountingTestProducer<String>()
        val consumer = ManualTestConsumer<String>()

        assertEquals(0, producer.produceCount)
        assertEquals(0, consumer.requestReadCount)

        // action
        var observedData = ""
        producer observeTransfer { observedData = it } sinkTo consumer
        producer.value = "a"
        producer.emitReadRequest()

        // effect
        assertEquals(0, producer.produceCount)
        assertEquals(1, consumer.requestReadCount)
        assertEquals("", observedData)

        // action
        consumer.performRead()

        // effect
        assertEquals(1, producer.produceCount)
        assertEquals("a", consumer.value)
        assertEquals("a", observedData)
    }

    private open class ManualTestConsumer<T> : BaseConsumer<T>() {
        public var requestReadCount: Int = 0
        public var value: T = null

        override fun consume(): (() -> Unit)? = {
            requestReadCount++
        }

        public fun performRead() {
            value = producer!!.produce()
        }
    }
}

