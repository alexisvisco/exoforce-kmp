package com.exoforce.component.helpers

import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.arkivanov.decompose.value.update
import com.exoforce.data.repository.LocalizedError
import com.exoforce.data.repository.getLocalizedMessageId
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.StringResource

/**
 * Represents the state of data loading with type-safe data access
 */
sealed class DataState<out T> {
    data object Loading : DataState<Nothing>()
    data class Success<T>(val data: T) : DataState<T>()
    data class Error(val messageId: StringResource?) : DataState<Nothing>()
}

/**
 * Strategy for loading data
 */
enum class DataLoadingStrategy {
    /**
     * Load local data first (if available), then fetch remote data.
     * onSuccess will be called twice if local data is available.
     */
    LOCAL_THEN_REMOTE,

    /**
     * Try to fetch remote data first. Only use local data as fallback if remote fails.
     * onSuccess will be called only once.
     */
    REMOTE_FALLBACK_LOCAL
}

/**
 * Generic data holder with loading and error states
 */
class DataHolder<T>(
    onSuccess: ((T) -> Unit)? = null,
    onError: ((StringResource?) -> Unit)? = null
) {
    private val _state = MutableValue<DataState<T>>(DataState.Loading)

    val state: Value<DataState<T>> = _state

    init {
        _state.subscribe { it ->
            when (it) {
                is DataState.Success -> onSuccess?.invoke(it.data)
                is DataState.Error -> onError?.invoke(it.messageId)
                else -> {}
            }
        }
    }

    /**
     * Load data with error handling
     *
     * @param coroutineScope The coroutine scope to launch the operation
     * @param localDataProvider Optional function to provide local/cached data immediately
     * @param remoteDataProvider Function to fetch remote data
     * @param strategy Strategy to use for loading data (default: LOCAL_THEN_REMOTE)
     */
    fun load(
        coroutineScope: CoroutineScope,
        localDataProvider: (suspend () -> T?)? = null,
        remoteDataProvider: suspend () -> Result<T>,
        strategy: DataLoadingStrategy = DataLoadingStrategy.LOCAL_THEN_REMOTE
    ) {
        coroutineScope.launch {
            _state.update { DataState.Loading }

            when (strategy) {
                DataLoadingStrategy.LOCAL_THEN_REMOTE -> {
                    // Load local data first if available
                    localDataProvider?.let { provider ->
                        provider()?.let { data ->
                            _state.update { DataState.Success(data) }
                        }
                    }

                    // Then fetch remote data
                    val result = remoteDataProvider()

                    if (result.isSuccess) {
                        result.getOrNull()?.let { data ->
                            _state.update { DataState.Success(data) }
                        }
                    } else {
                        val error = result.exceptionOrNull()
                        if (error is LocalizedError) {
                            _state.update { DataState.Error(error.getLocalizedMessageId()) }
                        } else {
                            _state.update { DataState.Error(null) }
                        }
                    }
                }

                DataLoadingStrategy.REMOTE_FALLBACK_LOCAL -> {
                    // Try remote first
                    val result = remoteDataProvider()

                    if (result.isSuccess) {
                        result.getOrNull()?.let { data ->
                            _state.update { DataState.Success(data) }
                        }
                    } else {
                        // Remote failed, try local as fallback
                        val localData = localDataProvider?.invoke()
                        if (localData != null) {
                            _state.update { DataState.Success(localData) }
                        } else {
                            // Both failed, show error
                            val error = result.exceptionOrNull()
                            if (error is LocalizedError) {
                                _state.update { DataState.Error(error.getLocalizedMessageId()) }
                            } else {
                                _state.update { DataState.Error(null) }
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Update data manually
     */
    fun updateData(newData: T) {
        _state.update { DataState.Success(newData) }
    }

    /**
     * Clear error state
     */
    fun clearError() {
        if (_state.value is DataState.Error) {
            _state.update { DataState.Loading }
        }
    }
}
