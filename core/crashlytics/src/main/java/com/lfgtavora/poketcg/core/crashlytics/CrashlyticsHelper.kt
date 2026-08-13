package com.lfgtavora.poketcg.core.crashlytics

/**
 * Interface for recording non-fatal crashes and breadcrumbs.
 * See `FirebaseCrashlyticsHelper` and `StubCrashlyticsHelper` for implementations.
 *
 * Keep this surface tiny. Feature-specific context belongs in extension functions
 * on the call site (same pattern as `AnalyticsHelper.logCardClicked`).
 */
interface CrashlyticsHelper {
    fun log(message: String)
    fun recordException(throwable: Throwable)
    fun setCustomKey(key: String, value: String)
}

fun CrashlyticsHelper.recordException(
    throwable: Throwable,
    extras: Map<String, String>,
) {
    extras.forEach { (key, value) -> setCustomKey(key, value) }
    recordException(throwable)
}
