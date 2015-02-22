package com.seraph.pipeline

/**
 * CTL
 * Created by seraph on 23.02.2015 0:01.
 */

public trait Consumer<in T> {
    public fun write(value:T)
}