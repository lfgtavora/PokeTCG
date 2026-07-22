package com.lfgtavora.poketcg.data.mapper

import com.google.common.truth.Truth.assertThat
import com.lfgtavora.poketcg.network.model.LegalitiesResponse
import com.lfgtavora.poketcg.network.model.SetImages
import com.lfgtavora.poketcg.network.model.SetResponse
import org.junit.Test

class SetMapperTest {

    @Test
    fun `asEntity maps null name totals and releaseDate to empty or zero`() {
        val entity = SetResponse(
            id = "sv1",
            name = null,
            printedTotal = null,
            total = null,
            releaseDate = null,
            legalitiesResponse = null,
        ).asEntity()

        assertThat(entity.name).isEmpty()
        assertThat(entity.printedTotal).isEqualTo(0)
        assertThat(entity.total).isEqualTo(0)
        assertThat(entity.releaseDate).isEmpty()
        assertThat(entity.legalities?.standard).isNull()
        assertThat(entity.legalities?.expanded).isNull()
        assertThat(entity.legalities?.unlimited).isNull()
    }

    @Test
    fun `asEntity maps legalities and images`() {
        val entity = SetResponse(
            id = "sv1",
            name = "Scarlet & Violet",
            printedTotal = 198,
            total = 258,
            releaseDate = "2023/03/31",
            legalitiesResponse = LegalitiesResponse(
                standard = "Legal",
                expanded = "Legal",
                unlimited = "Legal",
            ),
            images = SetImages(symbol = "sym.png", logo = "logo.png"),
        ).asEntity()

        assertThat(entity.name).isEqualTo("Scarlet & Violet")
        assertThat(entity.printedTotal).isEqualTo(198)
        assertThat(entity.total).isEqualTo(258)
        assertThat(entity.symbol).isEqualTo("sym.png")
        assertThat(entity.logo).isEqualTo("logo.png")
        assertThat(entity.legalities?.standard).isEqualTo("Legal")
    }
}
