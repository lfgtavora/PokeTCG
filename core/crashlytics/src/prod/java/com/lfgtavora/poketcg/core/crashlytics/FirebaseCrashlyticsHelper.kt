package com.lfgtavora.poketcg.core.crashlytics

import com.google.firebase.crashlytics.FirebaseCrashlytics
import javax.inject.Inject

/**
 * Prod implementation: forwards to Firebase Crashlytics.
 */
internal class FirebaseCrashlyticsHelper @Inject constructor(
    private val firebaseCrashlytics: FirebaseCrashlytics,
) : CrashlyticsHelper {

    override fun log(message: String) {
        firebaseCrashlytics.log(message)
    }

    override fun recordException(throwable: Throwable) {
        firebaseCrashlytics.recordException(throwable)
    }

    override fun setCustomKey(key: String, value: String) {
        firebaseCrashlytics.setCustomKey(
            key.take(MAX_KEY_LENGTH),
            value.take(MAX_VALUE_LENGTH),
        )
    }

    private companion object {
        const val MAX_KEY_LENGTH = 64
        const val MAX_VALUE_LENGTH = 1024
    }
}
