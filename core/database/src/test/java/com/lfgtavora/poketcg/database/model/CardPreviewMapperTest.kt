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
