/**
 * KMP-compatible mapper for converting data classes.
 *
 * ⚠️ IMPORTANT: kotlin.reflect.full is NOT available for iOS Native!
 * This version uses only what's actually available in KMP stdlib.
 *
 * Since we can't use automatic reflection on iOS, this is a hybrid approach:
 * - Type-safe custom mappings (works everywhere)
 * - Manual field mapping (explicit and maintainable)
 */
object DataClassMapper {

    /**
     * Creates a type-safe mapper with known source and target types.
     * All mappings must be done manually.
     *
     * @param from Source object (type is inferred)
     * @param factory Factory function to create target instance
     */
    inline fun <reified S : Any, reified T : Any> bind(
        from: S,
        noinline factory: () -> T
    ): TypedMapperBuilder<S, T> {
        return TypedMapperBuilder(from, factory)
    }

    /**
     * Direct mapping with a single mapping function
     */
    inline fun <reified S : Any, reified T : Any> bind(
        from: S,
        noinline factory: () -> T,
        noinline mapping: (S, T) -> Unit
    ): T {
        val instance = factory()
        mapping(from, instance)
        return instance
    }
}

/**
 * Builder with type-safe custom mappings
 * @param S Source type (known at compile time)
 * @param T Target type (known at compile time)
 */
class TypedMapperBuilder<S : Any, T : Any>(
    private val source: S,
    private val factory: () -> T
) {
    private val mappingFunctions = mutableListOf<(S, T) -> Unit>()

    /**
     * Adds a mapping function with known types.
     * No casting needed - full autocomplete available!
     *
     * @param mapping Function that receives typed source and target
     */
    fun map(mapping: (S, T) -> Unit): TypedMapperBuilder<S, T> {
        mappingFunctions.add(mapping)
        return this
    }

    /**
     * Builds the final object by applying all mappings
     */
    fun build(): T {
        val instance = factory()
        mappingFunctions.forEach { it(source, instance) }
        return instance
    }
}

/**
 * Extension function for fluent syntax with full type inference
 */
inline fun <reified S : Any, reified T : Any> S.mapTo(
    noinline factory: () -> T
): TypedMapperBuilder<S, T> {
    return DataClassMapper.bind(this, factory)
}

/**
 * Extension function for direct mapping with type safety
 */
inline fun <reified S : Any, reified T : Any> S.mapTo(
    noinline factory: () -> T,
    noinline mapping: (S, T) -> Unit
): T {
    return DataClassMapper.bind(this, factory, mapping)
}