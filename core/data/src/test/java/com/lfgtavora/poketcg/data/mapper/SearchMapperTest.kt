package com.lfgtavora.poketcg.data.mapper

import com.google.common.truth.Truth.assertThat
import com.lfgtavora.poketcg.model.data.SearchResultItem
import com.lfgtavora.poketcg.network.model.SearchCardImages
import com.lfgtavora.poketcg.network.model.SearchItemResponse
import com.lfgtavora.poketcg.network.model.SearchSetImages
import com.lfgtavora.poketcg.network.model.SearchSetInfo
import org.junit.Test

class SearchMapperTest {

    @Test
    fun `asSearchResultItem maps card with small image and nested set defaults`() {
        val result = SearchItemResponse.Card(
            id = "sv1-1",
            name = "Pikachu",
            set = SearchSetInfo(id = "sv1", name = "Scarlet"),
            images = SearchCardImages(small = "small.png", large = "large.png"),
        ).asSearchResultItem()

        assertThat(result).isInstanceOf(SearchResultItem.Card::class.java)
        val card = result as SearchResultItem.Card
        assertThat(card.id).isEqualTo("sv1-1")
        assertThat(card.image).isEqualTo("small.png")
        assertThat(card.set!!.id).isEqualTo("sv1")
        assertThat(card.set!!.logo).isNull()
        assertThat(card.set!!.series).isNull()
    }

    @Test
    fun `asSearchResultItem maps set with logo and series`() {
        val result = SearchItemResponse.Set(
            id = "sv1",
            name = "Scarlet & Violet",
            series = "Scarlet & Violet",
            images = SearchSetImages(logo = "logo.png", symbol = "sym.png"),
        ).asSearchResultItem()

        assertThat(result).isInstanceOf(SearchResultItem.Set::class.java)
        val set = result as SearchResultItem.Set
        assertThat(set.logo).isEqualTo("logo.png")
        assertThat(set.series).isEqualTo("Scarlet & Violet")
    }
}
