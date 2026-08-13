package com.lfgtavora.poketcg.core.crashlytics

/**
 * Recording fake for unit tests in other modules.
 *
 * ```
 * val crashlytics = TestCrashlyticsHelper()
 * val viewModel = SearchViewModel(repository, crashlytics)
 * // ...
 * assertThat(crashlytics.exceptions).hasSize(1)
 * ```
 */
class TestCrashlyticsHelper : CrashlyticsHelper {
    private val _logs = mutableListOf<String>()
    private val _exceptions = mutableListOf<Throwable>()
    private val _keys = linkedMapOf<String, String>()

    val logs: List<String> get() = _logs
    val exceptions: List<Throwable> get() = _exceptions
    val keys: Map<String, String> get() = _keys

    override fun log(message: String) {
        _logs += message
    }

    override fun recordException(throwable: Throwable) {
        _exceptions += throwable
    }

    override fun setCustomKey(key: String, value: String) {
        _keys[key] = value
    }
}
