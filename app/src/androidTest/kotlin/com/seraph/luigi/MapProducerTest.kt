package com.seraph.luigi

import junit.framework.TestCase
import kotlin.test.assertEquals

/**
 * Luigi
 * Created by Alexander Naumov on 08.04.2015.
 */

public class MapProducerTest : TestCase() {

    public fun test_mapProducer_requestsRead_whenOneOfItSourcesRequestsRead() {
        val src = Buffer(5)
        val countingConsumer = CountingTestConsumer<String>()

        // action
        src map { it.toString() } sinkTo countingConsumer
        countingConsumer.requestReadCount = 0
        7 bindTo src

        // effect
        assertEquals(1, countingConsumer.requestReadCount);
        assertEquals("7", countingConsumer.value)
    }

    public fun test_mapProducer_for1Source() {
        val src = Buffer(5)

        // action
        val dst = src map { it.toString() }

        // effect
        assertEquals("5", dst.read())

        // action
        7 bindTo src

        // effect
        assertEquals("7", dst.read())
    }

    public fun test_mapProducer_for2Sources() {
        val src1 = Buffer(2)
        val src2 = Buffer(3)

        // action
        val dst = src1 and src2 map { a, b -> a * b }

        // effect
        assertEquals(6, dst.read())

        // action
        7 bindTo src1

        // effect
        assertEquals(21, dst.read())

        // action
        5 bindTo src2

        // effect
        assertEquals(35, dst.read())
    }

    public fun test_mapProducer_for3Sources() {
        val src1 = Buffer("a")
        val src2 = Buffer("b")
        val src3 = Buffer("c")

        // action
        val dst = src1 and src2 and src3 map { a, b, c -> a + b + c }

        // effect
        assertEquals("abc", dst.read())

        // action
        "A" bindTo src1

        // effect
        assertEquals("Abc", dst.read())

        // action
        "B" bindTo src2

        // effect
        assertEquals("ABc", dst.read())

        // action
        "Kabum!" bindTo src3

        // effect
        assertEquals("ABKabum!", dst.read())
    }

    public fun test_mapProducer_for4Sources() {
        val src1 = Buffer("Sun")
        val src2 = Buffer("Mercury")
        val src3 = Buffer("Venus")
        val src4 = Buffer("Earth")

        // action
        val dst = src1 and src2 and src3 and src4 map { a, b, c, d -> "$a $b $c $d" }

        // effect
        assertEquals("Sun Mercury Venus Earth", dst.read())

        // action
        "Small" bindTo src1
        "step"  bindTo src2
        "for"   bindTo src3
        "man"   bindTo src4

        // effect
        assertEquals("Small step for man", dst.read()) // but huge leap for mankind
    }
}
