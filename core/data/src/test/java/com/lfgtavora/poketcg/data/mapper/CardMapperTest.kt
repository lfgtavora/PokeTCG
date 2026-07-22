package com.lfgtavora.poketcg.data.mapper

import com.google.common.truth.Truth.assertThat
import com.lfgtavora.poketcg.network.model.CardResponse
import com.lfgtavora.poketcg.network.model.LegalitiesResponse
import org.junit.Test

class CardMapperTest {

    @Test
    fun `asEntity maps nulls to empty defaults`() {
        val entity = CardResponse(
            id = "sv1-1",
            name = "Pikachu",
            number = "1",
            supertype = null,
            set = null,
            images = null,
            attacks = null,
            weaknesses = null,
        ).asEntity()

        assertThat(entity.supertype).isEmpty()
        assertThat(entity.setId).isEmpty()
        assertThat(entity.imageSmall).isNull()
        assertThat(entity.imageLarge).isNull()
        assertThat(entity.attacks).isNull()
        assertThat(entity.weaknesses).isNull()
        assertThat(entity.types).isNull()
        assertThat(entity.flavorText).isNull()
    }

    @Test
    fun `asEntity maps nested attack and weakness nulls to empty strings`() {
        val entity = CardResponse(
            id = "sv1-1",
            name = "Pikachu",
            number = "1",
            attacks = listOf(
                CardResponse.AttackResponse(
                    name = null,
                    cost = null,
                    text = null,
                    damage = "10",
                    convertedEnergyCost = 1,
                )
            ),
            weaknesses = listOf(
                CardResponse.WeaknessResponse(type = null, value = null)
            ),
            legalities = LegalitiesResponse(standard = "Legal", expanded = null, unlimited = "Legal"),
            images = CardResponse.ImageResponse(small = "s.png", large = "l.png"),
            set = com.lfgtavora.poketcg.network.model.SetResponse(id = "sv1", name = "Scarlet"),
        ).asEntity()

        assertThat(entity.attacks).hasSize(1)
        assertThat(entity.attacks!![0].name).isEmpty()
        assertThat(entity.attacks!![0].cost).isNull()
        assertThat(entity.weaknesses!![0].type).isEmpty()
        assertThat(entity.weaknesses!![0].value).isEmpty()
        assertThat(entity.setId).isEqualTo("sv1")
        assertThat(entity.imageSmall).isEqualTo("s.png")
        assertThat(entity.imageLarge).isEqualTo("l.png")
        assertThat(entity.legalities?.standard).isEqualTo("Legal")
    }
}
