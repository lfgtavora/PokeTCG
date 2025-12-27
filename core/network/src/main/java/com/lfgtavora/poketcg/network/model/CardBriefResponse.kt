package com.lfgtavora.poketcg.network.model

import kotlinx.serialization.Serializable

@Serializable
data class CardBriefResponse(
    val id: String,
    val image: String,
    val localId: String,
    val name: String
)
