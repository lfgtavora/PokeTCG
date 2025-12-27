package com.lfgtavora.poketcg.network.model

import kotlinx.serialization.Serializable

@Serializable
data class BoosterResponse(
    val id: String,
    val name: String,
    val logo: String? = null,
    val artworkFront: String? = null,
    val artworkBack: String? = null
)