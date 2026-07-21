package com.lfgtavora.poketcg.data.repository

import com.lfgtavora.poketcg.model.SearchResultItem

interface SearchRepository {
    suspend fun search(
        query: String,
        types: String = "card,set",
    ): List<SearchResultItem>
}
