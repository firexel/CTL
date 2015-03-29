package com.seraph.luigi

import junit.framework.TestCase
import kotlin.test.assertTrue
import kotlin.test.assertEquals
import kotlin.test.fail
import kotlin.test.assertFalse

/**
 * Luigi
 * Created by seraph on 23.02.2015 0:07.
 */

public class EventProducerTest : TestCase() {
    public fun test_eventProducer_storesValueTillFirstRead() {
        val p = EventProducer<Int>()
        var triggered = false

        assertFalse(p.armed)

        p.observe { triggered = true }
        p.fire(12)

        assertTrue(p.armed)
        assertTrue(triggered)
        assertEquals(12, p.read())
        assertFalse(p.armed)

        try {
            p.read()
            fail()
        } catch (e: Producer.NoDataException) {
            // pass
        }
    }

    public fun test_eventProducer_doNotStoreValueTillNotConnected() {
        val p = EventProducer<String>()

        assertFalse(p.armed)

        p.fire("a")

        assertFalse(p.armed)
    }
}