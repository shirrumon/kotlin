/*
 * Copyright 2010-2023 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.analysis.low.level.api.fir.caches

import com.intellij.openapi.application.ApplicationManager
import org.jetbrains.kotlin.analysis.low.level.api.fir.LLFirInternals
import java.lang.ref.ReferenceQueue
import java.lang.ref.SoftReference
import java.util.concurrent.ConcurrentHashMap

/**
 * [SoftValueCleaner] performs a cleaning operation after its associated value has been removed from the cache or garbage-collected. The
 * cleaner will be strongly referenced from the soft references held by the cache.
 *
 * You **must not** store a reference to the associated value [V] in its [SoftValueCleaner]. Otherwise, the cached values will never become
 * softly reachable.
 *
 * The cleaner may be invoked multiple times by the cache, in any thread. Implementations of [SoftValueCleaner] must ensure that the
 * operation is repeatable and thread-safe.
 */
@LLFirInternals
fun interface SoftValueCleaner<V> {
    /**
     * Cleans up after [value] has been removed from the cache or garbage-collected.
     *
     * [value] is non-null if it was removed from the cache and is still referable, or `null` if it has already been garbage-collected.
     */
    fun cleanUp(value: V?)
}

/**
 * A cache with hard references to its keys [K] and soft references to its values [V], which will be cleaned up after manual removal and
 * garbage collection. The cache should only be used in read/write actions, as specified by the individual functions.
 *
 * Each value of the cache has a [SoftValueCleaner] associated with it. The cache ensures that this cleaner is invoked when the value is
 * removed from or replaced in the cache, or when the value has been garbage-collected. Already collected values from the cache's reference
 * queue are guaranteed to be processed on mutating operations (such as `put`, `remove`, and so on). The [SoftValueCleaner] will be strongly
 * referenced from the cache until collected values have been processed.
 *
 * `null` keys or values are not allowed.
 *
 * @param getCleaner Returns the [SoftValueCleaner] that should be invoked after [V] has been collected or removed from the cache. The
 *  function will be invoked once when the value is added to the cache.
 */
@LLFirInternals
class CleanableSoftValueCache<K : Any, V : Any>(
    private val getCleaner: (V) -> SoftValueCleaner<V>,
) {
    private val backingMap = ConcurrentHashMap<K, SoftReferenceWithCleanup<K, V>>()

    private val referenceQueue = ReferenceQueue<V>()

    private fun processQueue() {
        while (true) {
            val ref = referenceQueue.poll() ?: break
            check(ref is SoftReferenceWithCleanup<*, *>)

            @Suppress("UNCHECKED_CAST")
            ref as SoftReferenceWithCleanup<K, V>

            backingMap.remove(ref.key, ref)
            ref.performCleanup()
        }
    }

    /**
     * Returns a value for the given [key] if it exists in the map. Must be called from a read action.
     */
    fun get(key: K): V? = backingMap[key]?.get()

    /**
     * If [key] is currently absent, attempts to add a value computed by [f] to the cache. [f] is invoked exactly once if [key] is present,
     * and otherwise never. Must be called in a read action.
     *
     * @return The already present or newly computed value associated with [key].
     */
    fun computeIfAbsent(key: K, f: (K) -> V): V {
        get(key)?.let { return it }

        return compute(key) { _, currentValue -> currentValue ?: f(key) }
            ?: error("`computeIfAbsent` should always return a non-null value.")
    }

    /**
     * Replaces the current value at [key] with a new value computed by [f]. [f] is invoked exactly once. Must be called in a read action.
     *
     * If the cache already contains a value `v` at [key], cleanup will be performed on it, *unless* the result of the computation is
     * referentially equal to `v`. This behavior enables computation functions to decide to retain an existing value, without triggering
     * cleanup.
     *
     * @return The computed value now associated with [key].
     */
    fun compute(key: K, f: (K, V?) -> V?): V? {
        // We need to keep a potentially newly computed value on the stack so that it isn't accidentally garbage-collected before the end of
        // this function. Without this variable, after `backingMap.compute` and before the end of this function, the soft reference kept in
        // the cache might be the only reference to the new value. With unlucky GC timing, it might be collected.
        var newValue: V? = null

        // If we replace an existing reference, we need to clean it up per the contract of the cache.
        var removedRef: SoftReferenceWithCleanup<K, V>? = null

        val newRef = backingMap.compute(key) { _, currentRef ->
            // If `currentRef` exists but its value is `null`, to the outside it will look like no value existed in the cache. It will be
            // cleaned up at the end of `compute`.
            val currentValue = currentRef?.get()
            newValue = f(key, currentValue)

            when {
                newValue == null -> {
                    removedRef = currentRef
                    null
                }

                // Avoid creating another soft reference for the same value, for example if `f` doesn't need to change the cached value,
                // though it isn't necessary for correct functioning of the cache. If there are multiple soft references for the same value,
                // they will all remain valid until the value itself is garbage-collected. Cleanup in `processQueue` will be performed once
                // for each such soft reference, which will result in multiple cleanup calls. This is legal given the contract of
                // `SoftValueCleaner`, but wasteful and thus best to avoid. Also, we shouldn't clean up such a reference, as per the
                // contract of the `compute` function.
                newValue === currentValue -> currentRef

                else -> {
                    removedRef = currentRef
                    createSoftReference(key, newValue!!)
                }
            }
        }

        removedRef?.performCleanup()
        processQueue()

        require(newRef?.get() === newValue) {
            "The newly computed value was already garbage-collected before the end of the `compute` function."
        }

        return newValue
    }

    /**
     * Adds or replaces [value] to/in the cache at the given [key]. Must be called in a read action.
     *
     * @return The old value that has been replaced, if any. As replacement constitutes removal, the cleaner associated with the value will
     * be invoked by [put].
     */
    fun put(key: K, value: V): V? {
        val oldRef = backingMap.put(key, createSoftReference(key, value))
        oldRef?.performCleanup()

        processQueue()
        return oldRef?.get()
    }

    /**
     * Removes the value associated with [key] from the cache, performs cleanup on it, and returns it if it exists. Must be called in a read
     * action.
     */
    fun remove(key: K): V? {
        val ref = backingMap.remove(key)
        ref?.performCleanup()

        processQueue()
        return ref?.get()
    }

    /**
     * Removes all values from the cache and performs cleanup on them. Must be called in a *write* action.
     *
     * The write action requirement is due to the complexity associated with atomically clearing a concurrent cache while also performing
     * cleanup on exactly the cleared values. Because this cache implementation is used by components which operate in read and write
     * actions, requiring a write action is more economical than synchronizing on some cache-wide lock.
     */
    fun clear() {
        ApplicationManager.getApplication().assertWriteAccessAllowed()

        // The backing map will not be modified by other threads during `clean` because it is executed in a write action.
        backingMap.values.forEach { it.performCleanup() }
        backingMap.clear()

        processQueue()
    }

    /**
     * Returns the number of elements in the cache. Must be called in a read action.
     */
    val size: Int
        get() {
            processQueue()
            return backingMap.size
        }

    /**
     * Returns whether the cache is empty. Must be called in a read action.
     */
    fun isEmpty(): Boolean {
        processQueue()
        return backingMap.isEmpty()
    }

    /**
     * Returns a snapshot of all keys in the cache. Changes to the cache do not reflect in the resulting set. Must be called in a read
     * action.
     */
    val keys: Set<K>
        get() {
            // Process the reference queue first to avoid returning keys whose values have already been garbage-collected (and should thus
            // not be part of the cache when viewed from the outside).
            processQueue()
            return backingMap.keys.toSet()
        }

    override fun toString(): String = "${this::class.simpleName} size:$size"

    private fun createSoftReference(key: K, value: V) = SoftReferenceWithCleanup(key, value, getCleaner(value), referenceQueue)

    private fun SoftReferenceWithCleanup<K, V>.performCleanup() {
        cleaner.cleanUp(get())
    }
}

private class SoftReferenceWithCleanup<K, V>(
    val key: K,
    value: V,
    val cleaner: SoftValueCleaner<V>,
    referenceQueue: ReferenceQueue<V>,
) : SoftReference<V>(value, referenceQueue) {
    override fun equals(other: Any?): Boolean {
        // When the referent is collected, equality should be identity-based (for `processQueue` to remove this very same soft value).
        // Hence, we skip the value equality check if the referent has been collected and `get()` returns `null`. If the reference is still
        // valid, this is just a canonical equals on referents for `replace(K,V,V)`.
        //
        // The `cleaner` is not part of equality, because `value` equality implies `cleaner` equivalence.
        if (this === other) return true
        if (other == null || other !is SoftReferenceWithCleanup<*, *>) return false
        if (key != other.key) return false

        val value = get() ?: return false
        return value == other.get()
    }

    override fun hashCode(): Int = key.hashCode()
}
