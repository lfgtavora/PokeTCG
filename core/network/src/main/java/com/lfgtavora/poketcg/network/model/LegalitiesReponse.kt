package com.lfgtavora.poketcg.network.model

import kotlinx.serialization.Serializable

@Serializable
data class LegalitiesResponse(
    val standard: String? = null,
    val expanded: String? = null,
    val unlimited: String? = null
)