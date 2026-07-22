package com.lfgtavora.poketcg.model

data class SetModel(
    val id: String,
    val name: String,
    val series: String? = null,
    val printedTotal: Int,
    val total: Int,
    val ptcgoCode: String? = null,
    val releaseDate: String,
    val updatedAt: String? = null,
    val symbol: String? = null,
    val logo: String? = null,
    val legalities: Legalities? = null,
)

data class Legalities(
    val standard: String? = null,
    val expanded: String? = null,
    val unlimited: String? = null
)
