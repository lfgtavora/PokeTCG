package com.lfgtavora.poketcg.database.model

import androidx.room.Embedded
import androidx.room.Relation

/**
 * This class captures the relationship between a [SetEntity] and its [SerieEntity].
 */
data class SetWithSerie(
    @Embedded
    val set: SetEntity,
    @Relation(
        parentColumn = "serieId",
        entityColumn = "id"
    )
    val serie: SerieEntity
)