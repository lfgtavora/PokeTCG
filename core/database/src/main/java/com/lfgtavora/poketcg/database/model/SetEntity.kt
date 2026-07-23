package com.lfgtavora.poketcg.database.model

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.lfgtavora.poketcg.model.data.SetModel
import com.lfgtavora.poketcg.model.data.SetPreview

@Entity(
    tableName = "sets",
    indices = [
        Index(value = ["series"])
    ]
)
data class SetEntity(
    @PrimaryKey
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
    @Embedded(prefix = "legal_")
    val legalities: Legalities? = null,
) {
    data class Legalities(
        val standard: String? = null,
        val expanded: String? = null,
        val unlimited: String? = null
    )
}

fun SetEntity.asPreviewModel() =
    SetPreview(
        id = id,
        name = name,
        logo = logo,
        releaseDate = releaseDate,
        totalCards = total
    )

fun SetEntity.asModel() =
    SetModel(
        id = id,
        name = name,
        series = series,
        printedTotal = printedTotal,
        total = total,
        ptcgoCode = ptcgoCode,
        releaseDate = releaseDate,
        updatedAt = updatedAt,
        symbol = symbol,
        logo = logo,
        legalities = com.lfgtavora.poketcg.model.data.Legalities(
            standard = legalities?.standard,
            expanded = legalities?.expanded,
            unlimited = legalities?.unlimited
        )
    )