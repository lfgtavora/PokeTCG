package com.lfgtavora.poketcg.data.mapper

import com.lfgtavora.poketcg.database.model.AttackData
import com.lfgtavora.poketcg.database.model.CardEntity
import com.lfgtavora.poketcg.database.model.Legalities
import com.lfgtavora.poketcg.database.model.WeaknessData
import com.lfgtavora.poketcg.network.model.CardResponse

fun CardResponse.asEntity(): CardEntity =
    CardEntity(
        id = id,
        name = name,
        supertype = supertype.orEmpty(), // CardEntity supertype is non-nullable
        subtypes = subtypes,
        number = number,
        artist = artist,
        rarity = rarity,
        hp = hp,
        evolvesTo = evolvesTo,
        setId = set?.id.orEmpty(), // Map set.id to setId, default to empty string if null
        imageSmall = images?.small,
        imageLarge = images?.large,
        legalities = legalities?.let {
            Legalities(
                standard = it.standard,
                expanded = it.expanded,
                unlimited = it.unlimited
            )
        },
        rules = rules,
        weaknesses = weaknesses?.map {
            WeaknessData(
                type = it.type.orEmpty(),
                value = it.value.orEmpty()
            )
        },
        retreatCost = retreatCost,
        convertedRetreatCost = convertedRetreatCost,
        nationalPokedexNumbers = nationalPokedexNumbers,
        attacks = attacks?.map {
            AttackData(
                name = it.name.orEmpty(),
                cost = it.cost,
                text = it.text,
                damage = it.damage,
                convertedEnergyCost = it.convertedEnergyCost
            )
        },
        // Fields in CardEntity not present in CardResponse will be null or default values
        flavorText = null,
        types = null, // CardResponse does not have 'types'
        evolvesFrom = null,
        level = null,
        regulationMark = null,
        resistances = null,
        abilities = null,
        ancientTrait = null
    )