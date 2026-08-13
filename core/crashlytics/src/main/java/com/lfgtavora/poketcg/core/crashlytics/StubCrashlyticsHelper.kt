package com.lfgtavora.poketcg.core.crashlytics

import android.util.Log
import javax.inject.Inject
import javax.inject.Singleton

private const val TAG = "StubCrashlyticsHelper"

/**
 * Demo implementation: dumps breadcrumbs and exceptions to logcat.
 * Bound only by the demo Hilt module.
 */
@Singleton
internal class StubCrashlyticsHelper @Inject constructor() : CrashlyticsHelper {
    override fun log(message: String) {
        Log.d(TAG, message)
    }

    override fun recordException(throwable: Throwable) {
        Log.e(TAG, "Non-fatal", throwable)
    }

    override fun setCustomKey(key: String, value: String) {
        Log.d(TAG, "key[$key]=$value")
    }
}
