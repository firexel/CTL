package com.seraph.luigi

import junit.framework.TestCase
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.failsWith

/**
 * Luigi
 * Created by Alexander Naumov on 09.06.2015.
 */
public class MergeTest : TestCase() {
    public fun test_merge_shouldTakeFirstAvailableValue() {
        val producer1 = CountingTestProducer(5)
        val producer2 = CountingTestProducer(7)
        val mergeProducer:Producer<Int> = merge(producer1, producer2)
        assertEquals(5, mergeProducer.produce())
        assertEquals(1, producer1.produceCount)
        assertEquals(0, producer2.produceCount)
    }

    public fun test_merge_shouldUseOtherProducers_ifFirstOnesContainingNoData() {
        assertEquals(7, merge(NoDataTestProducer<Int>(), CountingTestProducer(7)).produce())
    }

    public fun test_merge_shouldThrowNoData_ifAllOfProducersDontHaveAnyData() {
        val producer = merge(NoDataTestProducer<Int>(), NoDataTestProducer<Int>(), NoDataTestProducer<Int>())
        failsWith(javaClass<NoDataException>()) {
            producer.produce()
        }
    }

    public fun test_merge_shouldNotAllowToPassZeroProducers() {
        failsWith(javaClass<IllegalArgumentException>()) {
            merge<Unit>()
        }
    }

    private class NoDataTestProducer<T>() : BaseProducer<T>() {
        override fun produce(): T {
            throw NoDataException("Test")
        }
    }
}