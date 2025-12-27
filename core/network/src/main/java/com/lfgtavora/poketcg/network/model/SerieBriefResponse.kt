package com.lfgtavora.poketcg.network.model

import kotlinx.serialization.Serializable

@Serializable
data class SerieBriefResponse(
    val id: String,
    val name: String,
)