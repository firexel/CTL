package com.seraph.luigi

/**
 * Luigi
 * Created by Alexander Naumov on 08.04.2015.
 */

public class MapProducer<O>(producers: List<Producer<Any?>>, readDelegate: () -> O) : BaseProducer<O>() {
    private val readDelegate: () -> O = readDelegate;

    init {
        for (producer in producers) {
            producer sinkTo NotifierConsumer<Any?>()
        }
    }

    override fun read(): O = readDelegate()

    private inner class NotifierConsumer<T> : BaseConsumer<T>() {
        override fun requestRead() = consumer?.requestRead()
    }
}

public fun <I, O> Producer<I>.map(converter: (I) -> O): Producer<O> {
    return MapProducer(listOf(this)) { converter(read()) }
}

public fun <I1, I2> Producer<I1>.and(second: Producer<I2>): ProducerTupleOf2<I1, I2> {
    return ProducerTupleOf2(this, second)
}

public class ProducerTupleOf2<I1, I2>(p1: Producer<I1>, p2: Producer<I2>) {
    private val p1 = p1;
    private val p2 = p2;

    public fun <O> map(converter: (I1, I2) -> O): Producer<O> {
        return MapProducer(listOf(p1, p2)) { converter(p1.read(), p2.read()) }
    }

    public fun <I3> and(p3: Producer<I3>): ProducerTupleOf3<I1, I2, I3> {
        return ProducerTupleOf3(p1, p2, p3)
    }
}

public class ProducerTupleOf3<I1, I2, I3>(p1: Producer<I1>, p2: Producer<I2>, p3: Producer<I3>) {
    private val p1 = p1;
    private val p2 = p2;
    private val p3 = p3;

    public fun <O> map(converter: (I1, I2, I3) -> O): Producer<O> {
        return MapProducer(listOf(p1, p2, p3)) { converter(p1.read(), p2.read(), p3.read()) }
    }

    public fun <I4> and(p4: Producer<I4>): ProducerTupleOf4<I1, I2, I3, I4> {
        return ProducerTupleOf4(p1, p2, p3, p4)
    }
}

public class ProducerTupleOf4<I1, I2, I3, I4>(p1: Producer<I1>, p2: Producer<I2>, p3: Producer<I3>, p4: Producer<I4>) {
    private val p1 = p1;
    private val p2 = p2;
    private val p3 = p3;
    private val p4 = p4;

    public fun <O> map(converter: (I1, I2, I3, I4) -> O): Producer<O> {
        return MapProducer(listOf(p1, p2, p3, p4)) { converter(p1.read(), p2.read(), p3.read(), p4.read()) }
    }
}

