package com.exoforce.core.utils

sealed class Optional <out T> {
    data class Some<T>(val value: T) : Optional<T>()
    data object None : Optional<Nothing>()
}

fun <T> T?.toOptional(): Optional<T> {
    return if (this != null) {
        Optional.Some(this)
    } else {
        Optional.None
    }
}

fun <T> Optional<T>.getOrNull(): T? {
    return when (this) {
        is Optional.Some -> this.value
        is Optional.None -> null
    }
}

fun <T> Optional<T>.isSome(): Boolean {
    return this is Optional.Some
}

fun <T> Optional<T>.isNone(): Boolean {
    return this is Optional.None
}

fun <T, R> Optional<T>.map(transform: (T) -> R): Optional<R> {
    return when (this) {
        is Optional.Some -> Optional.Some(transform(this.value))
        is Optional.None -> Optional.None
    }
}

fun <T> Optional<T>.fold(onNone: () -> Unit, onSome: (T) -> Unit) {
    when (this) {
        is Optional.Some -> onSome(this.value)
        is Optional.None -> onNone()
    }
}

fun <T> Optional<T>.getOrElse(defaultValue: () -> T): T {
    return when (this) {
        is Optional.Some -> this.value
        is Optional.None -> defaultValue()
    }
}

fun <T> Optional<T>.getOrThrow(exception: () -> Throwable): T {
    return when (this) {
        is Optional.Some -> this.value
        is Optional.None -> throw exception()
    }
}

fun <T> Optional<T>.ifSome(action: (T) -> Unit): Optional<T> {
    if (this is Optional.Some) {
        action(this.value)
    }
    return this
}

fun <T> Optional<T>.ifNone(action: () -> Unit): Optional<T> {
    if (this is Optional.None) {
        action()
    }
    return this
}

fun <T> optionalOf(value: T?): Optional<T> {
    return if (value != null) {
        Optional.Some(value)
    } else {
        Optional.None
    }
}