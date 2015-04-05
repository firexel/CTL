package com.seraph.luigi

import junit.framework.TestCase
import kotlin.test.assertEquals

/**
 * Luigi
 * Created by Alexander Naumov on 05.04.2015.
 */

public class BufferTest : TestCase() {
    public fun test_buffer_new() {
        val buffer = Buffer(5)
        val producer = CountingTestProducer<Int>()

        // preconditions
        assertEquals(5, buffer.read())
        assert(buffer is Consumer<Int>)
        assert(buffer is Producer<Int>)
        assertEquals(null, producer.value)
        assertEquals(0, producer.readCount)

        // action
        42 bindTo buffer

        // effect
        assertEquals(42, buffer.read())

        // action
        producer.value = 7
        producer sinkTo buffer

        // effect
        assertEquals(0, producer.readCount)
        assertEquals(7, buffer.read())
        assertEquals(7, buffer.read())
        assertEquals(1, producer.readCount)

        // action
        producer.readCount = 0
        5.times { buffer.requestRead() }

        // effect
        assertEquals(0, producer.readCount)
        assertEquals(7, buffer.read())
        assertEquals(7, buffer.read())
        assertEquals(1, producer.readCount)
    }
}

private class CountingTestProducer<T> : BaseProducer<T>() {
    public var readCount: Int = 0
    public var value: T = null

    override fun read(): T {
        readCount++
        return value
    }
}