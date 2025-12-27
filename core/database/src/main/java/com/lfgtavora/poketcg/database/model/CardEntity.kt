package com.lfgtavora.poketcg.database.model

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "cards",
    foreignKeys = [
        ForeignKey(
            entity = SetEntity::class,
            parentColumns = ["id"],
            childColumns = ["setId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["setId"]),
        Index(value = ["category"])
    ]
)
data class CardEntity(
    @PrimaryKey
    val id: String,
    val localId: String,
    val name: String,
    val image: String?,
    val category: String,
    val illustrator: String?,
    val rarity: String?,
    val updated: String?,
    val setId: String,
    @Embedded(prefix = "variant_")
    val variants: VariantData?,

    /**
     * Pokemon Specific Properties
     */
    val hp: Int? = null,
    val types: List<String>? = null,
    val evolveFrom: String? = null,
    val description: String? = null,
    val level: String? = null,
    val stage: String? = null,
    val suffix: String? = null,
    val regulationMark: String? = null,
    val retreat: Int? = null,
    val dexId: List<Int>? = null,
    val attacks: List<AttackData>? = null,
    val weaknesses: List<WeaknessData>? = null,
    val resistances: List<ResistanceData>? = null,
    @Embedded(prefix = "item_")
    val item: ItemData? = null,

    /**
     * Trainer / Energy Specific Properties
     */
    val effect: String? = null,
    val trainerType: String? = null,
    val energyType: String? = null

)

data class ItemData(
    val name: String,
    val effect: String
)

data class VariantData(
    val normal: Boolean,
    val reverse: Boolean,
    val holo: Boolean,
    val firstEdition: Boolean
)

data class AttackData(
    val name: String,
    val cost: List<String>?,
    val effect: String?,
    val damage: String?
)

data class WeaknessData(
    val type: String,
    val value: String
)

data class ResistanceData(
    val type: String,
    val value: String
)