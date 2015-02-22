package com.seraph.pipeline

import junit.framework.TestCase
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * CTL
 * Created by seraph on 23.02.2015 0:34.
 */

public class MappingProducerTest : TestCase() {
    public fun test_mappingProducer_appliesTransformation() {
        val p = Buffer(0)
        val mp = MappingProducer(p) { it.toString() }
        assertEquals("0", mp.read())

        var triggered = false
        mp.observe { triggered = true }
        p.write(12)
        assertTrue(triggered)
        assertEquals("12", mp.read())
    }

    public fun test_mappingProducer_providesExtensionFunctionForAllProducers() {
        val p: Producer<Int> = Buffer(1)
        assertTrue(p.map { it.toString() } is MappingProducer<*,*>)
    }
}