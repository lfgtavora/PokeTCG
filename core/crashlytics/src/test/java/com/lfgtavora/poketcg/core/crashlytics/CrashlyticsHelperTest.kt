package com.lfgtavora.poketcg.core.crashlytics

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class CrashlyticsHelperTest {

    private val crashlytics = TestCrashlyticsHelper()

    @Test
    fun `recordException with extras writes keys then throwable`() {
        val error = IllegalStateException("sync failed")

        crashlytics.recordException(
            throwable = error,
            extras = mapOf(
                "set_id" to "sv1",
                "page" to "2",
            ),
        )

        assertThat(crashlytics.keys).containsExactly(
            "set_id", "sv1",
            "page", "2",
        )
        assertThat(crashlytics.exceptions).containsExactly(error)
    }

    @Test
    fun `log and keys are recorded in order`() {
        crashlytics.setCustomKey("screen", "search")
        crashlytics.log("query submitted")

        assertThat(crashlytics.keys).containsExactly("screen", "search")
        assertThat(crashlytics.logs).containsExactly("query submitted")
        assertThat(crashlytics.exceptions).isEmpty()
    }
}
