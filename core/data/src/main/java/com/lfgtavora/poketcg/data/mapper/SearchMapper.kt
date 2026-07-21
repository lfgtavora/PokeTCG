package com.lfgtavora.poketcg.data.mapper

import com.lfgtavora.poketcg.model.SearchResultItem
import com.lfgtavora.poketcg.network.model.SearchItemResponse

fun SearchItemResponse.asSearchResultItem(): SearchResultItem =
    when (this) {
        is SearchItemResponse.Card -> SearchResultItem.Card(
            id = id,
            name = name,
            image = images?.small,
            set = SearchResultItem.Set(
                id = set.id,
                name = set.name,
                logo = null,
                series = null,
            ),
        )
        is SearchItemResponse.Set -> SearchResultItem.Set(
            id = id,
            name = name,
            logo = images?.logo,
            series = series,
        )
    }
