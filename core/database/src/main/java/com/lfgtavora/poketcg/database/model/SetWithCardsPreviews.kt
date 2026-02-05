package com.lfgtavora.poketcg.database.model

import androidx.room.Embedded
import androidx.room.Relation
import com.lfgtavora.poketcg.model.SetWithCardsPreviewModel

data class SetWithCardsPreviews(
    @Embedded
    val set: SetEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "setId",
        entity = CardEntity::class
    )
    val cardsPreview: List<CardPreviewEntity>
)

fun SetWithCardsPreviews.asModel() =
    SetWithCardsPreviewModel(
        set = set.asModel(),
        cardsPreview = cardsPreview.map { it.asModel() }
    )