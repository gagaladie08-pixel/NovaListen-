// core/common/Result.kt

package com.novalisten.app.core.common

/**
 * Wrapper universel pour tous les résultats dans NovaListen.
 * Utilisé par les Use Cases et les Repositories.
 *
 * Principe Uncle Bob : on ne laisse pas les exceptions
 * traverser toutes les couches — on les encapsule proprement.
 */
sealed class Result<out T> {

    /** Opération réussie avec données */
    data class Success<T>(val data: T) : Result<T>()

    /** Erreur avec message lisible + cause optionnelle */
    data class Error(
        val message: String,
        val cause: Throwable? = null
    ) : Result<Nothing>()

    /** Chargement en cours */
    data object Loading : Result<Nothing>()

    // ── Extensions utiles ────────────────────────────────────────────────

    val isSuccess get() = this is Success
    val isError   get() = this is Error
    val isLoading get() = this is Loading

    fun getOrNull(): T? = if (this is Success) data else null

    fun getOrDefault(default: @UnsafeVariance T): T =
        if (this is Success) data else default

    fun onSuccess(action: (T) -> Unit): Result<T> {
        if (this is Success) action(data)
        return this
    }

    fun onError(action: (Error) -> Unit): Result<T> {
        if (this is Error) action(this)
        return this
    }
}