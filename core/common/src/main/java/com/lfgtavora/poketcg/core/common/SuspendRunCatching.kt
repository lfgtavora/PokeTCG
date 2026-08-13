package com.lfgtavora.poketcg.core.common

import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlin.coroutines.cancellation.CancellationException

/**
 *  Runs the block and returns the result as a [Result].
 *  @param block the block to run
 *  @return the result of the block
 *  @throws CancellationException if the coroutine was cancelled
 *  @throws Exception if the block throws an exception
 */
suspend inline fun <T, R> T.suspendRunCatching(block: T.() -> R): Result<R> {
    return try {
        currentCoroutineContext().ensureActive()
        Result.success(block())
    } catch (ce: CancellationException) {
        throw ce
    } catch (e: Throwable) {
        Result.failure(e)
    }
}