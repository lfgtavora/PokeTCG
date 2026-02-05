package com.lfgtavora.poketcg.database.model

import com.lfgtavora.poketcg.model.CardPreview

data class CardPreviewEntity(
    val id: String,
    val name: String,
    val imageSmall: String? = null,
    val imageLarge: String? = null,
    val setId: String
)

fun CardPreviewEntity.asModel() =
    CardPreview(
        id = id,
        name = name,
        image = imageLarge,
        setId = setId
    )