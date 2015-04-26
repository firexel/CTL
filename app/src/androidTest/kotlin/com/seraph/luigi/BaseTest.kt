package com.seraph.luigi

import junit.framework.TestCase
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.failsWith

/**
 * Luigi
 * Created by Alexander Naumov on 05.04.2015.
 */

public class BaseTest : TestCase() {
    public fun test_sinkToExtension() {
        val producer = TestBaseProducer<String>()
        val consumerProducer = TestBaseConsumerProducer<String, Int>()
        val consumer = TestBaseConsumer<Int>()

        // preconditions
        producer.assertConsumerEquals(null)
        consumer.assertProducerEquals(null)

        // action
        producer sinkTo consumerProducer sinkTo consumer

        // effect
        producer.assertConsumerEquals(consumerProducer)
        consumerProducer.assertProducerEquals(producer)
        consumerProducer.assertConsumerEquals(consumer)
        consumer.assertProducerEquals(consumerProducer)
    }

    public fun test_baseConsumer_basics() {
        performConsumerBaseTest(TestBaseConsumer<String>())
    }

    public fun test_baseProducer_basics() {
        performProducerBaseTest(TestBaseProducer<String>())
    }

    public fun test_baseConsumerProducer_basics() {
        performConsumerBaseTest(TestBaseConsumerProducer<String, String>())
        performProducerBaseTest(TestBaseConsumerProducer<String, String>())
    }

    private fun performConsumerBaseTest(consumer: TestConsumer<String>) {
        val producer1: Producer<String> = TestBaseProducer()

        // preconditions
        assertTrue(consumer is Consumer<*>)
        consumer.assertProducerEquals(null)

        // actions
        consumer.bindProducer(producer1)
        consumer.assertProducerEquals(producer1)

        failsWith(javaClass<AlreadyBeingBoundException>(), { consumer.bindProducer(TestBaseProducer()) })

        assertEquals(producer1, consumer.unbindProducer())
        consumer.assertProducerEquals(null)

        assertEquals(null, consumer.unbindProducer())
    }

    private fun performProducerBaseTest(producer: TestProducer<String>) {
        val consumer1 = TestBaseConsumer<String>()

        // preconditions
        assertTrue(producer is Producer<*>)
        producer.assertConsumerEquals(null)

        // actions
        producer.bindConsumer(consumer1)
        producer.assertConsumerEquals(consumer1)

        failsWith(javaClass<AlreadyBeingBoundException>(), { producer.bindConsumer(TestBaseConsumer()) })

        assertEquals(consumer1, producer.unbindConsumer())
        producer.assertConsumerEquals(null)

        assertEquals(null, producer.unbindConsumer())
    }
}

private trait TestConsumer<T> : Consumer<T> {
    public fun assertProducerEquals(producer: Producer<T>?)
    public fun assertProducer(predicate: (Producer<T>?) -> Boolean)
}

private trait TestProducer<T> : Producer<T> {
    public fun assertConsumerEquals(consumer: Consumer<T>?)
}

private open class TestBaseConsumer<T> : BaseConsumer<T>(), TestConsumer<T> {
    override fun requestRead() {
        throw UnsupportedOperationException()
    }

    public override fun assertProducerEquals(producer: Producer<T>?) {
        assertEquals(producer, this.producer)
    }

    override fun assertProducer(predicate: (Producer<T>?) -> Boolean) {
        assertTrue(predicate(this.producer))
    }
}

private open class TestBaseProducer<T> : BaseProducer<T>(), TestProducer<T> {
    override fun read(): T {
        throw UnsupportedOperationException()
    }

    public override fun assertConsumerEquals(consumer: Consumer<T>?) {
        assertEquals(consumer, this.consumer)
    }
}

private open class TestBaseConsumerProducer<I, O> : BaseConsumerProducer<I, O>(), TestConsumer<I>, TestProducer<O> {
    override fun read(): O {
        throw UnsupportedOperationException()
    }

    override fun requestRead() {
        throw UnsupportedOperationException()
    }

    override fun assertProducerEquals(producer: Producer<I>?) {
        assertEquals(producer, this.producer)
    }

    override fun assertProducer(predicate: (Producer<I>?) -> Boolean) {
        assertTrue(predicate(this.producer))
    }

    override fun assertConsumerEquals(consumer: Consumer<O>?) {
        assertEquals(consumer, this.consumer)
    }
}
