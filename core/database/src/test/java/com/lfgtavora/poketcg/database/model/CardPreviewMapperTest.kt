package com.lfgtavora.poketcg.database.model

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class CardPreviewMapperTest {

    @Test
    fun `CardEntity asCardPreview uses imageSmall`() {
        val preview = CardEntity(
            id = "sv1-1",
            name = "Pikachu",
            supertype = "Pokémon",
            number = "1",
            setId = "sv1",
            imageSmall = "small.png",
            imageLarge = "large.png",
        ).asCardPreview()

        assertThat(preview.image).isEqualTo("small.png")
    }

    @Test
    fun `CardEntity asCard maps nested fields`() {
        val card = CardEntity(
            id = "base1-4",
            name = "Charizard",
            supertype = "Pokémon",
            number = "4",
            setId = "base1",
            imageLarge = "large.png",
            attacks = listOf(
                AttackData(
                    name = "Fire Spin",
                    cost = listOf("Fire"),
                    text = null,
                    damage = "100",
                    convertedEnergyCost = 1,
                )
            ),
            weaknesses = listOf(WeaknessData(type = "Water", value = "×2")),
            abilities = listOf(AbilityData(name = "Burn", text = "text", type = "Ability")),
            legalities = Legalities(unlimited = "Legal"),
        ).asCard()

        assertThat(card.name).isEqualTo("Charizard")
        assertThat(card.imageLarge).isEqualTo("large.png")
        assertThat(card.attacks).hasSize(1)
        assertThat(card.attacks!![0].name).isEqualTo("Fire Spin")
        assertThat(card.weaknesses!![0].type).isEqualTo("Water")
        assertThat(card.abilities!![0].name).isEqualTo("Burn")
        assertThat(card.legalities?.unlimited).isEqualTo("Legal")
    }

    @Test
    fun `CardPreviewEntity asModel uses imageLarge`() {
        // Current behavior differs from CardEntity.asCardPreview(); lock it until unified.
        val preview = CardPreviewEntity(
            id = "sv1-1",
            name = "Pikachu",
            imageSmall = "small.png",
            imageLarge = "large.png",
            setId = "sv1",
        ).asModel()

        assertThat(preview.image).isEqualTo("large.png")
    }
}
