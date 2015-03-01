package com.seraph.pipeline

import junit.framework.TestCase
import kotlin.test.assertEquals

/**
 * CTL
 * Created by seraph on 02.03.2015 0:34.
 */

public class ConnectorTest : TestCase() {
    public fun test_connector_performsTransferWhenProducerUpdated() {
        val producer = Buffer("a")
        val consumer = Buffer("")

        Connector(producer, consumer)

        assertEquals("a", producer.read())
        assertEquals("a", consumer.read())

        producer.write("b")

        assertEquals("b", producer.read())
        assertEquals("b", consumer.read())
    }

    public fun test_connector_skipsTransferWhenNoDataExceptionThrown() {
        val producer = FaultyProducer()
        val consumer = Buffer("")

        Connector(producer, consumer)

        assertEquals(1, producer.timesRead)
        assertEquals("a", consumer.read())

        producer.invoke()

        assertEquals(2, producer.timesRead)
        assertEquals("a", consumer.read())
    }

    public fun test_connector_providesAnExtensionFunctionForProducer() {
        val producer = Buffer("a")
        val consumer = Buffer("")

        producer.sinkTo(consumer)

        assertEquals("a", producer.read())
        assertEquals("a", consumer.read())

        producer.write("b")

        assertEquals("b", producer.read())
        assertEquals("b", consumer.read())
    }

    private class FaultyProducer : ObservableProducer<String>() {
        public var timesRead: Int = 0

        override fun read(): String {
            try {
                if (timesRead <= 0) {
                    return "a"
                } else {
                    throw Producer.NoDataException()
                }
            } finally {
                timesRead++
            }
        }

        public fun invoke() {
            invokeObserver()
        }
    }
}

