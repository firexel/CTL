package com.seraph.luigi

import junit.framework.TestCase
import kotlin.test.assertTrue
import kotlin.test.assertEquals

/**
 * Luigi
 * Created by seraph on 22.02.2015 23:19.
 */

public class BufferTest : TestCase() {
    public fun test_buffer_storesValue() {
        val b = Buffer(0)
        var triggered = false
        b.observe {
            triggered = true
        }
        assertEquals(0, b.read())
        b.write(1)
        assertTrue(triggered)
        assertEquals(1, b.read())

        // assume data is still there after first read
        assertEquals(1, b.read())
    }
}