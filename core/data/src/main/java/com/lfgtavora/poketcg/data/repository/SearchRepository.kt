package com.lfgtavora.poketcg.data.repository

import com.lfgtavora.poketcg.model.data.SearchResultItem

interface SearchRepository {
    suspend fun search(
        query: String,
        types: String = "card,set",
    ): List<SearchResultItem>
}
