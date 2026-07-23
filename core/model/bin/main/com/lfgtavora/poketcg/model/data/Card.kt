package com.lfgtavora.poketcg.model.data

data class Card(
    val id: String,
    val name: String,
    val supertype: String,
    val subtypes: List<String>? = null,
    val number: String,
    val artist: String? = null,
    val rarity: String? = null,
    val flavorText: String? = null,
    val nationalPokedexNumbers: List<Int>? = null,
    val imageSmall: String? = null,
    val imageLarge: String? = null,
    val setId: String,
    val hp: String? = null,
    val types: List<String>? = null,
    val evolvesFrom: String? = null,
    val evolvesTo: List<String>? = null,
    val rules: List<String>? = null,
    val retreatCost: List<String>? = null,
    val convertedRetreatCost: Int? = null,
    val level: String? = null,
    val regulationMark: String? = null,
    val attacks: List<Attack>? = null,
    val weaknesses: List<Weakness>? = null,
    val resistances: List<Resistance>? = null,
    val abilities: List<Ability>? = null,
    val ancientTrait: AncientTrait? = null,
    val legalities: Legalities? = null,
)

data class Attack(
    val name: String,
    val cost: List<String>?,
    val text: String?,
    val damage: String?,
    val convertedEnergyCost: Int?,
)

data class Weakness(
    val type: String,
    val value: String,
)

data class Resistance(
    val type: String,
    val value: String,
)

data class Ability(
    val name: String,
    val text: String,
    val type: String,
)

data class AncientTrait(
    val name: String,
    val text: String,
)
