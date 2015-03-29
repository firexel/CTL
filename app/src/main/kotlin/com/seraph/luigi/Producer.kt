package com.seraph.luigi

/**
 * Luigi
 * Created by seraph on 22.02.2015 23:56.
 */

public trait Producer<out T> {
    public fun read(): T
    public fun observe(observer: () -> Unit)

    public class NoDataException : RuntimeException()
}
