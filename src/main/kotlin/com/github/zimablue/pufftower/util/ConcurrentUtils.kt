package com.github.zimablue.pufftower.util

import java.util.concurrent.atomic.AtomicReference
import java.util.function.BiPredicate


fun <V> testAndSet(reference: AtomicReference<V>, predicate: BiPredicate<V, V>, testValue: V, newValue: V): Boolean {
    while (true) {
        val prev = reference.get()
        if (predicate.test(prev, testValue)) {
            if (reference.compareAndSet(prev, newValue)) return true
        } else {
            return false
        }
    }
}

fun <V> testAndSet(reference: AtomicReference<V>, predicate: BiPredicate<V, V>, newValue: V): Boolean {
    return testAndSet(reference, predicate, newValue, newValue)
}