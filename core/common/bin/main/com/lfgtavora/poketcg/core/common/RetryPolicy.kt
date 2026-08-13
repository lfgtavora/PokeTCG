package com.lfgtavora.poketcg.core.common

import java.io.IOException
import kotlin.math.pow
import kotlin.random.Random

data class RetryPolicy(
    val maxAttempts: Int = 3,
    val initialDelayMs: Long = 500,
    val maxDelayMs: Long = 8_000,
    val factor: Double = 2.0,
    val jitterRatio: Double = 0.5,
    val shouldRetry: (Throwable) -> Boolean = { it is IOException },
) {
    init {
        require(maxAttempts >= 1) { "maxAttempts should be >= 1" }
    }

    fun delayMillis(attempt: Int): Long {
        val base = (initialDelayMs * factor.pow(attempt))
            .toLong()
            .coerceAtMost(maxDelayMs)
        val jitter = (base * jitterRatio).toLong()
        return base + Random.nextLong(0, jitter.coerceAtLeast(1))
    }

    companion object {
        val Default = RetryPolicy()
        val None = RetryPolicy(maxAttempts = 1)
        val Aggressive = RetryPolicy(maxAttempts = 5, initialDelayMs = 1_000)
    }
}