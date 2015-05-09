package com.seraph.luigi

import junit.framework.TestCase
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlin.test.failsWith

/**
 * Luigi
 * Created by aleksandr.naumov on 09.05.2015.
 */
public class ForkTest : TestCase() {
    public fun test_fork_whenProducerAlreadyBound() {
        val producer = CountingTestProducer(1)
        assertEquals(0, producer.produceCount)

        val consumer1 = CountingTestConsumer<Int>()
        assertEquals(0, consumer1.consumeCount)

        val consumer2 = CountingTestConsumer<Int>()
        assertEquals(0, consumer2.consumeCount)

        producer sinkTo consumer1
        assertEquals(1, producer.produceCount)
        assertEquals(1, consumer1.value)

        producer.fork() sinkTo consumer2
        assertEquals(3, producer.produceCount)
        assertEquals(1, consumer1.value)
        assertEquals(2, consumer1.consumeCount)
        assertEquals(1, consumer2.value)
        assertEquals(1, consumer2.consumeCount)

        producer.value = 77
        producer.emitReadRequest()
        assertEquals(5, producer.produceCount)
        assertEquals(77, consumer1.value)
        assertEquals(3, consumer1.consumeCount)
        assertEquals(77, consumer2.value)
        assertEquals(2, consumer2.consumeCount)
    }

    public fun test_fork_whenProducerNotBound() {
        val producer = CountingTestProducer(1)
        assertTrue(producer.fork() identityEquals producer)
    }

    public fun test_fork_whenItItselfNotBound() {
        val forkConsumer = ForkConsumer<Int>()
        assertNull(forkConsumer.consume())
        failsWith(javaClass<NoDataException>(), { forkConsumer.producer1.produce() })
        failsWith(javaClass<NoDataException>(), { forkConsumer.producer2.produce() })
    }
}