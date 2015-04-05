package com.seraph.luigi

import junit.framework.TestCase
import kotlin.test.assertEquals

/**
 * Luigi
 * Created by Alexander Naumov on 05.04.2015.
 */

public class BaseTest : TestCase() {
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
        val producer2: Producer<String> = TestBaseProducer()

        // preconditions
        assert(consumer is Consumer<*>)
        consumer.assertProducerEquals(null)

        // actions
        consumer.observe(producer1)
        consumer.assertProducerEquals(producer1)

        consumer.ignore(producer2)
        consumer.assertProducerEquals(producer1)

        consumer.ignore(producer1)
        consumer.assertProducerEquals(null)
    }

    private fun performProducerBaseTest(producer: TestProducer<String>) {
        val consumer1: Consumer<String> = TestBaseConsumer()
        val consumer2: Consumer<String> = TestBaseConsumer()

        // preconditions
        assert(producer is Producer<*>)
        producer.assertConsumerEquals(null)

        // actions
        producer.sinkTo(consumer1)
        producer.assertConsumerEquals(consumer1)

        producer.ignore(consumer2)
        producer.assertConsumerEquals(consumer1)

        producer.ignore(consumer1)
        producer.assertConsumerEquals(null)
    }
}

private trait TestConsumer<T> : Consumer<T> {
    public fun assertProducerEquals(producer: Producer<T>?)
}

private trait TestProducer<T> : Producer<T> {
    public fun assertConsumerEquals(consumer: Consumer<T>?)
}

private class TestBaseConsumer<T> : BaseConsumer<T>(), TestConsumer<T> {
    override fun requestRead() {
        // do nothing
    }

    public override fun assertProducerEquals(producer: Producer<T>?) {
        assertEquals(producer, this.producer)
    }
}

private class TestBaseProducer<T> : BaseProducer<T>(), TestProducer<T> {
    override fun read(): T {
        throw UnsupportedOperationException()
    }

    public override fun assertConsumerEquals(consumer: Consumer<T>?) {
        assertEquals(consumer, this.consumer)
    }
}

private class TestBaseConsumerProducer<I, O> : BaseConsumerProducer<I, O>(), TestConsumer<I>, TestProducer<O> {
    override fun read(): O {
        throw UnsupportedOperationException()
    }

    override fun requestRead() {
        throw UnsupportedOperationException()
    }

    override fun assertProducerEquals(producer: Producer<I>?) {
        assertEquals(producer, this.producer)
    }

    override fun assertConsumerEquals(consumer: Consumer<O>?) {
        assertEquals(consumer, this.consumer)
    }
}
