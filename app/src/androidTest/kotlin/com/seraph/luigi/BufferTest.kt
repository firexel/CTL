package com.seraph.luigi

import junit.framework.TestCase
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Luigi
 * Created by Alexander Naumov on 05.04.2015.
 */

public class BufferTest : TestCase() {
    public fun test_buffer_readBehaviour() {
        val buffer = Buffer(5)
        val producer = CountingTestProducer<Int>()
        val consumer = CountingTestConsumer<Int>()

        // preconditions
        assertEquals(5, buffer.produce())
        assertTrue(buffer is Consumer<Int>)
        assertTrue(buffer is Producer<Int>)
        assertEquals(null, producer.value)
        assertEquals(0, producer.readCount)
        assertEquals(null, consumer.value)
        assertEquals(0, consumer.consumeCount)

        // action
        42 bindTo buffer

        // effect
        assertEquals(42, buffer.produce())

        // action
        producer.value = 7
        buffer.unbindProducer()
        producer sinkTo buffer sinkTo consumer

        // effect
        assertEquals(1, producer.readCount)
        assertEquals(7, buffer.produce())
        assertEquals(1, producer.readCount)
        assertEquals(7, consumer.value)
        assertEquals(1, consumer.consumeCount)

        // action
        consumer.consumeCount = 0
        producer.readCount = 0
        5.times { buffer.consume()?.invoke() }

        // effect
        assertEquals(5, producer.readCount)
        assertEquals(7, buffer.produce())
        assertEquals(7, buffer.produce())
        assertEquals(5, producer.readCount)
        assertEquals(7, consumer.value)
        assertEquals(5, consumer.consumeCount)
    }
}

private open class CountingTestProducer<T> : BaseProducer<T>() {
    public var readCount: Int = 0
    public var value: T = null

    override fun produce(): T {
        readCount++
        return value
    }

    fun emitReadRequest() {
        consumer?.consume()?.invoke()
    }

    public fun retrieveConsumer(): Consumer<T>? {
        return consumer
    }
}

private open class CountingTestConsumer<T> : BaseConsumer<T>() {
    public var consumeCount: Int = 0
    public var value: T = null

    override fun bindProducer(producer: Producer<T>) {
        super.bindProducer(producer)
        consume()?.invoke()
    }

    override fun consume(): (() -> Unit)? = {
        consumeCount++
        value = producer!!.produce()
    }
}