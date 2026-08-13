package com.lfgtavora.poketcg.core.crashlytics

/**
 * Swallows everything. Use in Compose previews or tests that do not care about crashes.
 */
class NoOpCrashlyticsHelper : CrashlyticsHelper {
    override fun log(message: String) = Unit
    override fun recordException(throwable: Throwable) = Unit
    override fun setCustomKey(key: String, value: String) = Unit
}
