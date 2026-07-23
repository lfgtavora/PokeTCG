package com.lfgtavora.poketcg.model.data


data class SetPreview(
    val id: String,
    val name: String,
    val releaseDate: String?,
    val logo: String? = null,
    val totalCards: Int? = null,
)