package com.lfgtavora.poketcg.model.data

sealed interface SearchResultItem {
    data class Card(
        val id: String,
        val name: String,
        val image: String?,
        val set: Set?,
    ) : SearchResultItem

    data class Set(
        val id: String,
        val name: String,
        val logo: String?,
        val series: String?,
    ) : SearchResultItem
}
