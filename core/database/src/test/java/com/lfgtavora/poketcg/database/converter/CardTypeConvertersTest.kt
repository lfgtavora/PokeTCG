package com.lfgtavora.poketcg.database.converter

import com.google.common.truth.Truth.assertThat
import com.lfgtavora.poketcg.database.model.AbilityData
import com.lfgtavora.poketcg.database.model.AncientTraitData
import com.lfgtavora.poketcg.database.model.AttackData
import com.lfgtavora.poketcg.database.model.ResistanceData
import com.lfgtavora.poketcg.database.model.WeaknessData
import org.junit.Test

class CardTypeConvertersTest {

    private val converters = CardTypeConverters()

    @Test
    fun `string list null roundtrip`() {
        assertThat(converters.fromStringList(null)).isNull()
        assertThat(converters.toStringList(null)).isNull()
    }

    @Test
    fun `string list empty roundtrip`() {
        val encoded = converters.fromStringList(emptyList())
        assertThat(converters.toStringList(encoded)).isEmpty()
    }

    @Test
    fun `string list roundtrip`() {
        val source = listOf("Fire", "Water")
        assertThat(converters.toStringList(converters.fromStringList(source))).isEqualTo(source)
    }

    @Test
    fun `int list roundtrip`() {
        val source = listOf(25, 26)
        assertThat(converters.toIntList(converters.fromIntList(source))).isEqualTo(source)
    }

    @Test
    fun `attack list with null cost and text roundtrip`() {
        val source = listOf(
            AttackData(
                name = "Tackle",
                cost = null,
                text = null,
                damage = "10",
                convertedEnergyCost = 1,
            )
        )
        assertThat(converters.toAttackList(converters.fromAttackList(source))).isEqualTo(source)
    }

    @Test
    fun `weakness resistance ability and ancientTrait roundtrip`() {
        val weaknesses = listOf(WeaknessData(type = "Fire", value = "×2"))
        val resistances = listOf(ResistanceData(type = "Grass", value = "-20"))
        val abilities = listOf(AbilityData(name = "Overgrow", text = "text", type = "Ability"))
        val trait = AncientTraitData(name = "θ Stop", text = "text")

        assertThat(converters.toWeaknessList(converters.fromWeaknessList(weaknesses)))
            .isEqualTo(weaknesses)
        assertThat(converters.toResistanceList(converters.fromResistanceList(resistances)))
            .isEqualTo(resistances)
        assertThat(converters.toAbilityList(converters.fromAbilityList(abilities)))
            .isEqualTo(abilities)
        assertThat(converters.toAncientTrait(converters.fromAncientTrait(trait)))
            .isEqualTo(trait)
        assertThat(converters.fromAncientTrait(null)).isNull()
        assertThat(converters.toAncientTrait(null)).isNull()
    }
}
