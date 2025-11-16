import com.arkivanov.decompose.Cancellation
import com.arkivanov.decompose.value.Value
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

fun <A : Any, B : Any, U> combineValueToStateFlow(
    v1: Value<A>,
    v2: Value<B>,
    block: (A, B) -> U
): StateFlow<U> {
    return callbackFlow {
        val callback = {
            val combined = block(v1.value, v2.value)
            trySend(combined)
        }

        val cancels: List<Cancellation> = listOf(
            v1.subscribe { callback() },
            v2.subscribe { callback() }
        )

        callback()

        awaitClose {
            cancels.forEach { it.cancel() }
        }
    }.stateIn(
        scope = CoroutineScope(kotlinx.coroutines.Dispatchers.Default),
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = block(v1.value, v2.value)
    )
}

fun <A : Any, B : Any, C : Any, U> combineValueToStateFlow(
    v1: Value<A>,
    v2: Value<B>,
    v3: Value<C>,
    block: (A, B, C) -> U
): StateFlow<U> {
    return callbackFlow {
        val callback = {
            val combined = block(v1.value, v2.value, v3.value)
            trySend(combined)
        }

        val cancels: List<Cancellation> = listOf(
            v1.subscribe { callback() },
            v2.subscribe { callback() },
            v3.subscribe { callback() }
        )

        callback()

        awaitClose {
            cancels.forEach { it.cancel() }
        }
    }.stateIn(
        scope = CoroutineScope(kotlinx.coroutines.Dispatchers.Default),
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = block(v1.value, v2.value, v3.value)
    )
}

fun <A : Any, B : Any, C : Any, D : Any, U> combineValueToStateFlow(
    v1: Value<A>,
    v2: Value<B>,
    v3: Value<C>,
    v4: Value<D>,
    block: (A, B, C, D) -> U
): StateFlow<U> {
    return callbackFlow {
        val callback = {
            val combined = block(v1.value, v2.value, v3.value, v4.value)
            trySend(combined)
        }

        val cancels: List<Cancellation> = listOf(
            v1.subscribe { callback() },
            v2.subscribe { callback() },
            v3.subscribe { callback() },
            v4.subscribe { callback() }
        )

        callback()

        awaitClose {
            cancels.forEach { it.cancel() }
        }
    }.stateIn(
        scope = CoroutineScope(kotlinx.coroutines.Dispatchers.Default),
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = block(v1.value, v2.value, v3.value, v4.value)
    )
}

fun <A : Any, B : Any, C : Any, D : Any, E : Any, U> combineValueToStateFlow(
    v1: Value<A>,
    v2: Value<B>,
    v3: Value<C>,
    v4: Value<D>,
    v5: Value<E>,
    block: (A, B, C, D, E) -> U
): StateFlow<U> {
    return callbackFlow {
        val callback = {
            val combined = block(v1.value, v2.value, v3.value, v4.value, v5.value)
            trySend(combined)
        }

        val cancels: List<Cancellation> = listOf(
            v1.subscribe {
                println("DEBUG combineValueToStateFlow: v1 (execState) changed, new value: ${v1.value}")
                callback()
            },
            v2.subscribe {
                println("DEBUG combineValueToStateFlow: v2 (countdown.remainingSeconds) changed to: ${v2.value}")
                callback()
            },
            v3.subscribe {
                println("DEBUG combineValueToStateFlow: v3 (countdown.isPaused) changed to: ${v3.value}")
                callback()
            },
            v4.subscribe {
                println("DEBUG combineValueToStateFlow: v4 (stopwatch.elapsedSeconds) changed to: ${v4.value}")
                callback()
            },
            v5.subscribe {
                println("DEBUG combineValueToStateFlow: v5 (stopwatch.isPaused) changed to: ${v5.value}")
                callback()
            }
        )

        callback()

        awaitClose {
            cancels.forEach { it.cancel() }
        }
    }.stateIn(
        scope = CoroutineScope(kotlinx.coroutines.Dispatchers.Default),
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = block(v1.value, v2.value, v3.value, v4.value, v5.value)
    )
}

fun <A : Any, B : Any, C : Any, D : Any, E : Any, F : Any, U> combineValueToStateFlow(
    v1: Value<A>,
    v2: Value<B>,
    v3: Value<C>,
    v4: Value<D>,
    v5: Value<E>,
    v6: Value<F>,
    block: (A, B, C, D, E, F) -> U
): StateFlow<U> {
    return callbackFlow {
        val callback = {
            val combined = block(v1.value, v2.value, v3.value, v4.value, v5.value, v6.value)
            trySend(combined)
        }

        val cancels: List<Cancellation> = listOf(
            v1.subscribe { callback() },
            v2.subscribe { callback() },
            v3.subscribe { callback() },
            v4.subscribe { callback() },
            v5.subscribe { callback() },
            v6.subscribe { callback() }
        )

        callback()

        awaitClose {
            cancels.forEach { it.cancel() }
        }
    }.stateIn(
        scope = CoroutineScope(kotlinx.coroutines.Dispatchers.Default),
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = block(v1.value, v2.value, v3.value, v4.value, v5.value, v6.value)
    )
}

fun <A : Any, B : Any, C : Any, D : Any, E : Any, F : Any, G : Any, U> combineValueToStateFlow(
    v1: Value<A>,
    v2: Value<B>,
    v3: Value<C>,
    v4: Value<D>,
    v5: Value<E>,
    v6: Value<F>,
    v7: Value<G>,
    block: (A, B, C, D, E, F, G) -> U
): StateFlow<U> {
    return callbackFlow {
        val callback = {
            val combined = block(v1.value, v2.value, v3.value, v4.value, v5.value, v6.value, v7.value)
            trySend(combined)
        }

        val cancels: List<Cancellation> = listOf(
            v1.subscribe { callback() },
            v2.subscribe { callback() },
            v3.subscribe { callback() },
            v4.subscribe { callback() },
            v5.subscribe { callback() },
            v6.subscribe { callback() },
            v7.subscribe { callback() }
        )

        callback()

        awaitClose {
            cancels.forEach { it.cancel() }
        }
    }.stateIn(
        scope = CoroutineScope(kotlinx.coroutines.Dispatchers.Default),
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = block(v1.value, v2.value, v3.value, v4.value, v5.value, v6.value, v7.value)
    )
}

fun <A : Any, B : Any, C : Any, D : Any, E : Any, F : Any, G : Any, H : Any, U> combineValueToStateFlow(
    v1: Value<A>,
    v2: Value<B>,
    v3: Value<C>,
    v4: Value<D>,
    v5: Value<E>,
    v6: Value<F>,
    v7: Value<G>,
    v8: Value<H>,
    block: (A, B, C, D, E, F, G, H) -> U
): StateFlow<U> {
    return callbackFlow {
        val callback = {
            val combined = block(v1.value, v2.value, v3.value, v4.value, v5.value, v6.value, v7.value, v8.value)
            trySend(combined)
        }

        val cancels: List<Cancellation> = listOf(
            v1.subscribe { callback() },
            v2.subscribe { callback() },
            v3.subscribe { callback() },
            v4.subscribe { callback() },
            v5.subscribe { callback() },
            v6.subscribe { callback() },
            v7.subscribe { callback() },
            v8.subscribe { callback() }
        )

        callback()

        awaitClose {
            cancels.forEach { it.cancel() }
        }
    }.stateIn(
        scope = CoroutineScope(kotlinx.coroutines.Dispatchers.Default),
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = block(v1.value, v2.value, v3.value, v4.value, v5.value, v6.value, v7.value, v8.value)
    )
}

fun <A : Any, B : Any, C : Any, D : Any, E : Any, F : Any, G : Any, H : Any, I : Any, U> combineValueToStateFlow(
    v1: Value<A>,
    v2: Value<B>,
    v3: Value<C>,
    v4: Value<D>,
    v5: Value<E>,
    v6: Value<F>,
    v7: Value<G>,
    v8: Value<H>,
    v9: Value<I>,
    block: (A, B, C, D, E, F, G, H, I) -> U
): StateFlow<U> {
    return callbackFlow {
        val callback = {
            val combined =
                block(v1.value, v2.value, v3.value, v4.value, v5.value, v6.value, v7.value, v8.value, v9.value)
            trySend(combined)
        }

        val cancels: List<Cancellation> = listOf(
            v1.subscribe { callback() },
            v2.subscribe { callback() },
            v3.subscribe { callback() },
            v4.subscribe { callback() },
            v5.subscribe { callback() },
            v6.subscribe { callback() },
            v7.subscribe { callback() },
            v8.subscribe { callback() },
            v9.subscribe { callback() }
        )

        callback()

        awaitClose {
            cancels.forEach { it.cancel() }
        }
    }.stateIn(
        scope = CoroutineScope(kotlinx.coroutines.Dispatchers.Default),
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = block(v1.value, v2.value, v3.value, v4.value, v5.value, v6.value, v7.value, v8.value, v9.value)
    )
}

fun <A : Any, B : Any, C : Any, D : Any, E : Any, F : Any, G : Any, H : Any, I : Any, J : Any, U> combineValueToStateFlow(
    v1: Value<A>,
    v2: Value<B>,
    v3: Value<C>,
    v4: Value<D>,
    v5: Value<E>,
    v6: Value<F>,
    v7: Value<G>,
    v8: Value<H>,
    v9: Value<I>,
    v10: Value<J>,
    block: (A, B, C, D, E, F, G, H, I, J) -> U
): StateFlow<U> {
    return callbackFlow {
        val callback = {
            val combined = block(
                v1.value,
                v2.value,
                v3.value,
                v4.value,
                v5.value,
                v6.value,
                v7.value,
                v8.value,
                v9.value,
                v10.value
            )
            trySend(combined)
        }

        val cancels: List<Cancellation> = listOf(
            v1.subscribe { callback() },
            v2.subscribe { callback() },
            v3.subscribe { callback() },
            v4.subscribe { callback() },
            v5.subscribe { callback() },
            v6.subscribe { callback() },
            v7.subscribe { callback() },
            v8.subscribe { callback() },
            v9.subscribe { callback() },
            v10.subscribe { callback() }
        )

        callback()

        awaitClose {
            cancels.forEach { it.cancel() }
        }
    }.stateIn(
        scope = CoroutineScope(kotlinx.coroutines.Dispatchers.Default),
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = block(
            v1.value,
            v2.value,
            v3.value,
            v4.value,
            v5.value,
            v6.value,
            v7.value,
            v8.value,
            v9.value,
            v10.value
        )
    )
}
