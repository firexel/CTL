package com.seraph.luigi

import junit.framework.TestCase
import kotlin.test.assertEquals

/**
 * Luigi
 * Created by Alexander Naumov on 05.04.2015.
 */

public class ConstantProducerTest : TestCase() {
    public fun test_constantProducer_new() {
        val producer = ConstantProducer(5)
        assertEquals(5, producer.produce())
    }

    public fun test_constantProducer_extendsAllTypes() {
        val consumer = TestBaseConsumer<Int>()
        5 bindTo consumer;
        consumer.assertProducer { it is ConstantProducer<Int> }
    }
}
