package com.lfgtavora.poketcg.database.model

import androidx.room.Embedded
import androidx.room.Relation

data class SetWithCards(
    @Embedded
    val set: SetEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "setId"
    )
    val cards: List<CardEntity>
)