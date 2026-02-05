package com.lfgtavora.poketcg.database.model

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.lfgtavora.poketcg.model.CardPreview
import kotlinx.serialization.Serializable

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
        Index(value = ["supertype"])
    ]
)
data class CardEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val supertype: String,
    val subtypes: List<String>? = null,
    val number: String, // era localId
    val artist: String? = null,
    val rarity: String? = null,
    val flavorText: String? = null,
    val nationalPokedexNumbers: List<Int>? = null,
    val imageSmall: String? = null,
    val imageLarge: String? = null,
    val setId: String,

    /**
     * Pokemon Specific Properties
     */
    val hp: String? = null,
    val types: List<String>? = null,
    val evolvesFrom: String? = null,
    val evolvesTo: List<String>? = null,
    val rules: List<String>? = null,
    val retreatCost: List<String>? = null,
    val convertedRetreatCost: Int? = null,
    val level: String? = null,
    val regulationMark: String? = null,
    val attacks: List<AttackData>? = null,
    val weaknesses: List<WeaknessData>? = null,
    val resistances: List<ResistanceData>? = null,
    val abilities: List<AbilityData>? = null,
    val ancientTrait: AncientTraitData? = null,

    /**
     * Legalities
     */
    @Embedded(prefix = "legal_")
    val legalities: Legalities? = null
)

@Serializable
data class Legalities(
    val standard: String? = null,
    val expanded: String? = null,
    val unlimited: String? = null
)

@Serializable
data class AttackData(
    val name: String,
    val cost: List<String>?,
    val text: String?,
    val damage: String?,
    val convertedEnergyCost: Int?
)

@Serializable
data class WeaknessData(
    val type: String,
    val value: String
)

@Serializable
data class ResistanceData(
    val type: String,
    val value: String
)

@Serializable
data class AbilityData(
    val name: String,
    val text: String,
    val type: String
)

@Serializable
data class AncientTraitData(
    val name: String,
    val text: String
)

fun CardEntity.asCardPreview() = CardPreview(
    id = id,
    name = name,
    image = imageSmall,
    setId = setId
)