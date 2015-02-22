package com.seraph.pipeline

import junit.framework.TestCase
import kotlin.test.assertTrue
import kotlin.test.assertEquals
import kotlin.test.fail

/**
 * CTL
 * Created by seraph on 23.02.2015 0:07.
 */

public class EventProducerTest : TestCase() {
    public fun test_eventProducer_storesValueTillFirstRead() {
        val p = EventProducer<Int>()
        var triggered = false

        p.observe { triggered = true }
        p.fire(12)

        assertTrue(triggered)
        assertEquals(12, p.read())

        try {
            p.read()
            fail()
        } catch (e:Producer.NoDataException) {
            // pass
        }
    }
}