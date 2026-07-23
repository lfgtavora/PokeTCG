package com.lfgtavora.poketcg.feature.card_detail.impl.preview

import com.lfgtavora.poketcg.model.data.Ability
import com.lfgtavora.poketcg.model.data.Attack
import com.lfgtavora.poketcg.model.data.Card
import com.lfgtavora.poketcg.model.data.Legalities
import com.lfgtavora.poketcg.model.data.Resistance
import com.lfgtavora.poketcg.model.data.Weakness

internal val fakeAbility = Ability(
    name = "Energy Burn",
    text = "As often as you like during your turn (before your attack), you may turn all Energy attached to Charizard into Fire Energy for the rest of the turn.",
    type = "Pokémon Power",
)

internal val fakeAttack = Attack(
    name = "Fire Spin",
    cost = listOf("Fire", "Fire", "Fire", "Fire"),
    text = "Discard 2 Energy cards attached to Charizard in order to use this attack.",
    damage = "100",
    convertedEnergyCost = 4,
)

internal val fakeAttackNoDamage = Attack(
    name = "Confuse Ray",
    cost = listOf("Psychic"),
    text = "Flip a coin. If heads, the Defending Pokémon is now Confused.",
    damage = null,
    convertedEnergyCost = 1,
)

internal val fakeAttackMinimal = Attack(
    name = "Scratch",
    cost = null,
    text = null,
    damage = "10",
    convertedEnergyCost = 0,
)

internal val fakeInfoChips = listOf(
    "Pokémon",
    "Stage 2",
    "Fire",
    "Rare Holo",
    "Reg G",
)

internal val fakeInfoChipsWrapping = listOf(
    "Pokémon",
    "Stage 2",
    "Single Strike",
    "Fire",
    "Fighting",
    "Rare Holo VMAX",
    "Reg E",
    "Standard: Legal",
    "Expanded: Legal",
    "Unlimited: Legal",
)

internal val fakeCard = Card(
    id = "base1-4",
    name = "Charizard",
    supertype = "Pokémon",
    subtypes = listOf("Stage 2"),
    number = "4",
    artist = "Mitsuhiro Arita",
    rarity = "Rare Holo",
    flavorText = "Spits fire that is hot enough to melt boulders. Known to cause forest fires unintentionally.",
    nationalPokedexNumbers = listOf(6),
    imageSmall = "https://images.pokemontcg.io/base1/4.png",
    imageLarge = "https://images.pokemontcg.io/base1/4_hires.png",
    setId = "base1",
    hp = "120",
    types = listOf("Fire"),
    evolvesFrom = "Charmeleon",
    retreatCost = listOf("Colorless", "Colorless", "Colorless"),
    convertedRetreatCost = 3,
    level = "76",
    attacks = listOf(fakeAttack),
    weaknesses = listOf(Weakness(type = "Water", value = "×2")),
    resistances = listOf(Resistance(type = "Fighting", value = "-30")),
    abilities = listOf(fakeAbility),
    legalities = Legalities(
        unlimited = "Legal",
    ),
)
