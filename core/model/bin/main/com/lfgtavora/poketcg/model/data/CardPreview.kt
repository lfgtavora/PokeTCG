package com.lfgtavora.poketcg.model.data

data class CardPreview(
    val id: String,
    val name: String,
    val image: CardPreviewImage,
    val setId: String
)

data class CardPreviewImage(
    val small: String?,
    val large: String?,
)
