package com.lfgtavora.poketcg.database.model

import com.lfgtavora.poketcg.model.data.CardPreview
import com.lfgtavora.poketcg.model.data.CardPreviewImage

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
        image = CardPreviewImage(
            small = imageSmall,
            large = imageLarge
        ),
        setId = setId
    )