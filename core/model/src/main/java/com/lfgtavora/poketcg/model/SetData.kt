package com.lfgtavora.poketcg.model

data class SetData(
    val id: String,
    val name: String,
    val releaseDate: String,
    val logo: String? = null,
)