package com.lfgtavora.poketcg.network.model

import kotlinx.serialization.Serializable


@Serializable
data class SetResponse(
    val id: String,
    val name: String,
    val logo: String? = null,
    val symbol: String? = null,
    val cardCount: CardCountResponse,
    val serie: SerieBriefResponse,
    val tcgOnline: String? = null,
    val releaseDate: String,
    val legal: Legal,
    val boosters: List<BoosterResponse>? = null,
    val cards: List<CardBriefResponse>? = null
)

@Serializable
data class CardCountResponse(
    val total: Int,
    val official: Int,
    val reverse: Int,
    val holo: Int,
    val firstEd: Int
)


@Serializable
data class Legal(
    val standard: Boolean,
    val expanded: Boolean
)
