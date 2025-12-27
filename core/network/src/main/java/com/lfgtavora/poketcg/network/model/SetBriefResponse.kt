package com.lfgtavora.poketcg.network.model

import kotlinx.serialization.Serializable

@Serializable
data class SetBriefResponse(
    val id: String,
    val name: String,
    val logo: String? = null,
    val symbol: String? = null,
    val cardCount: CardCountBriefResponse
)

@Serializable
data class CardCountBriefResponse(
    val total: Int,
    val official: Int
)