package com.seraph.luigi

import junit.framework.TestCase
import kotlin.test.assertEquals

/**
 * Luigi
 * Created by Alexander Naumov on 08.04.2015.
 */

public class MapProducerTest : TestCase() {

    public fun test_mapProducer_agedConsumerCallbacks() {
        val producer = CountingTestProducer<String>()
        producer.value = "abc"
        assertEquals(0, producer.readCount)

        val consumer = CountingTestConsumer<Int>()
        assertEquals(0, consumer.consumeCount)

        producer map { it.length() } sinkTo consumer
        assertEquals(1, producer.readCount)
        assertEquals(1, consumer.consumeCount)
        assertEquals(3, consumer.value)

        sequence { producer.retrieveConsumer()?.consume() }.take(5).toArrayList().forEach { it?.invoke() }
        assertEquals(2, producer.readCount)
        assertEquals(2, consumer.consumeCount)

        5.times { producer.retrieveConsumer()?.consume()?.invoke() }
        assertEquals(7, producer.readCount)
        assertEquals(7, consumer.consumeCount)
    }

    public fun test_mapProducer_requestsRead_whenOneOfItSourcesRequestsRead() {
        val src = Buffer(5)
        val countingConsumer = CountingTestConsumer<String>()

        // action
        src map { it.toString() } sinkTo countingConsumer
        countingConsumer.consumeCount = 0
        7 bindTo src

        // effect
        assertEquals(1, countingConsumer.consumeCount);
        assertEquals("7", countingConsumer.value)
    }

    public fun test_mapProducer_for1Source() {
        val src = Buffer(5)

        // action
        val dst = src map { it.toString() }

        // effect
        assertEquals("5", dst.produce())

        // action
        7 bindTo src

        // effect
        assertEquals("7", dst.produce())
    }

    public fun test_mapProducer_for2Sources() {
        val src1 = Buffer(2)
        val src2 = Buffer(3)

        // action
        val dst = src1 and src2 map { a, b -> a * b }

        // effect
        assertEquals(6, dst.produce())

        // action
        7 bindTo src1

        // effect
        assertEquals(21, dst.produce())

        // action
        5 bindTo src2

        // effect
        assertEquals(35, dst.produce())
    }

    public fun test_mapProducer_for3Sources() {
        val src1 = Buffer("a")
        val src2 = Buffer("b")
        val src3 = Buffer("c")

        // action
        val dst = src1 and src2 and src3 map { a, b, c -> a + b + c }

        // effect
        assertEquals("abc", dst.produce())

        // action
        "A" bindTo src1

        // effect
        assertEquals("Abc", dst.produce())

        // action
        "B" bindTo src2

        // effect
        assertEquals("ABc", dst.produce())

        // action
        "Kabum!" bindTo src3

        // effect
        assertEquals("ABKabum!", dst.produce())
    }

    public fun test_mapProducer_for4Sources() {
        val src1 = Buffer("Sun")
        val src2 = Buffer("Mercury")
        val src3 = Buffer("Venus")
        val src4 = Buffer("Earth")

        // action
        val dst = src1 and src2 and src3 and src4 map { a, b, c, d -> "$a $b $c $d" }

        // effect
        assertEquals("Sun Mercury Venus Earth", dst.produce())

        // action
        "Small" bindTo src1
        "step"  bindTo src2
        "for"   bindTo src3
        "man"   bindTo src4

        // effect
        assertEquals("Small step for man", dst.produce()) // but huge leap for mankind
    }

    private data class Building(
            val name: String,
            val floors: Int,
            val area: Double,
            val country: String,
            val materials: Set<String>) {}

    public fun test_mapProducer_for5Sources() {
        val name = Buffer("Burj Khalifa")
        val floors = Buffer(163)
        val area = Buffer(309473.5)
        val country = Buffer("UAE")
        val materials = Buffer(setOf("steel", "glass", "concrete"))

        // action
        val building = name and floors and area and country and materials map ::Building

        // effect
        assertEquals(
                Building("Burj Khalifa", 163, 309473.5, "UAE", setOf("steel", "glass", "concrete")),
                building.produce()
        )
    }
}
