package com.lfgtavora.poketcg.network.model

import kotlinx.serialization.Serializable

@Serializable
data class CardDataListResponse(
    val data: List<CardResponse>,
    val page: Int,
    val pageSize: Int,
    val count: Int,
    val totalCount: Int
)

@Serializable
data class CardDataResponse(
    val data: CardResponse
)

@Serializable
data class CardResponse(
    val id: String,
    val name: String,
    val supertype: String? = null,
    val subtypes: List<String>? = null,
    val number: String,
    val artist: String? = null,
    val rarity: String? = null,
    val hp: String? = null,
    val evolvesTo: List<String>? = null,
    val set: SetResponse? = null,
    val images: ImageResponse? = null,
    val legalities: LegalitiesResponse? = null,
    val rules: List<String>? = null,
    val weaknesses: List<WeaknessResponse>? = null,
    val retreatCost: List<String>? = null,
    val convertedRetreatCost: Int? = null,
    val nationalPokedexNumbers: List<Int>? = null,
    val attacks: List<AttackResponse>? = null
) {
    @Serializable
    data class ImageResponse(
        val small: String? = null,
        val large: String? = null
    )

    @Serializable
    data class WeaknessResponse(
        val type: String? = null,
        val value: String? = null
    )

    @Serializable
    data class AttackResponse(
        val name: String? = null,
        val cost: List<String>? = null,
        val convertedEnergyCost: Int? = null,
        val damage: String? = null,
        val text: String? = null
    )
}


