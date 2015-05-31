package com.seraph.luigi

/**
 * Luigi
 * Created by Alexander Naumov on 08.04.2015.
 */

public class MapProducer<O>(producers: List<Producer<*>>, readDelegate: () -> O) : BaseProducer<O>() {
    private val readDelegate: () -> O = readDelegate;
    private synchronized var age: Int = 0

    init {
        for (producer in producers) {
            @suppress("UNCHECKED_CAST")
            ((producer as Producer<Any?>) sinkTo NotifierConsumer<Any?>())
        }
    }

    override synchronized fun produce(): O {
        age++
        return readDelegate()
    }

    private inner class NotifierConsumer<T> : BaseConsumer<T>() {
        override synchronized fun consume(): (() -> Unit)? {
            val consumeFunction = consumer?.consume()
            if (consumeFunction != null) {
                val consumerFunctionAge = age
                return {
                    if (age == consumerFunctionAge) {
                        consumeFunction.invoke()
                    }
                }
            } else {
                return null
            }
        }
    }
}

public fun <I, O> Producer<I>.map(converter: (I) -> O): Producer<O> {
    return MapProducer(listOf(this)) { converter(produce()) }
}

public fun <I1, I2> Producer<I1>.and(second: Producer<I2>): ProducerTupleOf2<I1, I2> {
    return ProducerTupleOf2(this, second)
}

public class ProducerTupleOf2<I1, I2>(p1: Producer<I1>, p2: Producer<I2>) {
    private val p1 = p1;
    private val p2 = p2;

    public fun <O> map(converter: (I1, I2) -> O): Producer<O> {
        return MapProducer(listOf(p1, p2)) { converter(p1.produce(), p2.produce()) }
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
        return MapProducer(listOf(p1, p2, p3)) { converter(p1.produce(), p2.produce(), p3.produce()) }
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
        return MapProducer(listOf(p1, p2, p3, p4)) { converter(p1.produce(), p2.produce(), p3.produce(), p4.produce()) }
    }

    public fun <I5> and(p5: Producer<I5>): ProducerTupleOf5<I1, I2, I3, I4, I5> {
        return ProducerTupleOf5(p1, p2, p3, p4, p5)
    }
}

public class ProducerTupleOf5<I1, I2, I3, I4, I5>(p1: Producer<I1>, p2: Producer<I2>, p3: Producer<I3>, p4: Producer<I4>, p5: Producer<I5>) {
    private val p1 = p1;
    private val p2 = p2;
    private val p3 = p3;
    private val p4 = p4;
    private val p5 = p5;

    public fun <O> map(converter: (I1, I2, I3, I4, I5) -> O): Producer<O> {
        return MapProducer(listOf(p1, p2, p3, p4, p5)) { converter(p1.produce(), p2.produce(), p3.produce(), p4.produce(), p5.produce()) }
    }
}

