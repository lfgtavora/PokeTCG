package com.lfgtavora.poketcg.data.mediator

fun interface TransactionRunner {
    suspend operator fun invoke(block: suspend () -> Unit)
}
