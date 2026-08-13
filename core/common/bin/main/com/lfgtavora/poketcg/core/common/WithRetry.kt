package com.lfgtavora.poketcg.core.common

import kotlinx.coroutines.delay
import kotlin.coroutines.cancellation.CancellationException


/**
 *  Retries the block a specified number of times with an exponential backoff.
 *
 *  @throws CancellationException if the coroutine was cancelled
 *  @throws Exception if the block throws an exception
 */
suspend fun <T> withRetry(
    policy: RetryPolicy = RetryPolicy.Default,
    block: suspend () -> T,
): T {
    repeat(policy.maxAttempts - 1) { attempt ->
        try {
            return block()
        } catch (ce: CancellationException) {
            throw ce
        } catch (e: Exception) {
            if (!policy.shouldRetry(e)) throw e
            delay(policy.delayMillis(attempt))
        }
    }
    return block()
}