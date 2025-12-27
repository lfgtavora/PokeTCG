package com.lfgtavora.poketcg.database.model

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.lfgtavora.poketcg.model.SetData

@Entity(
    tableName = "sets",
    foreignKeys = [
        ForeignKey(
            entity = SerieEntity::class,
            parentColumns = ["id"],
            childColumns = ["serieId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class SetEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val logo: String? = null,
    val symbol: String? = null,
    @Embedded(prefix = "card_count_")
    val cardCount: CardCount,
    val serieId: String,
    val tcgOnline: String? = null,
    val releaseDate: String,
    @Embedded
    val legal: Legal,
)

data class Legal(
    val standard: Boolean,
    val expanded: Boolean
)

data class CardCount(
    val total: Int,
    val official: Int,
    val reverse: Int,
    val holo: Int,
    val firstEd: Int
)

fun SetEntity.asModel() = SetData(
    id = id,
    name = name,
    logo = logo,
    releaseDate = releaseDate
)