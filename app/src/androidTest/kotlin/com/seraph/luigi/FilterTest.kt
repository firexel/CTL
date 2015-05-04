package com.seraph.luigi

import junit.framework.TestCase
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.failsWith

/**
 * Luigi
 * Created by Alexander Naumov on 04.05.2015.
 */

public class FilterTest : TestCase() {
    public fun test_filter_shouldNotifyConsumerOnlyAboutPositiveMatches() {
        val producer = CountingTestProducer<Int>()
        producer.value = 0
        assertEquals(0, producer.produceCount)

        val consumer = CountingTestConsumer<Int>()
        assertEquals(0, consumer.consumeCount)

        producer filter { it % 2 == 0 } sinkTo consumer
        assertEquals(0, consumer.value)
        assertEquals(1, consumer.consumeCount)
        assertEquals(1, producer.produceCount)

        producer.value = 3
        producer.emitReadRequest()
        assertEquals(0, consumer.value)
        assertEquals(1, consumer.consumeCount)
        assertEquals(2, producer.produceCount)

        producer.value = 8
        producer.emitReadRequest()
        assertEquals(8, consumer.value)
        assertEquals(2, consumer.consumeCount)
        assertEquals(3, producer.produceCount)
    }

    public fun test_filter_shouldThrowAnExceptionInProduceMethod_whenDataIsFilteredOut() {
        val producer = CountingTestProducer<Int>()
        producer.value = 0
        assertEquals(0, producer.produceCount)

        val filter = producer filter { false }
        assertEquals(0, producer.produceCount)

        failsWith(javaClass<NoDataException>(), { filter.produce() })
    }
}