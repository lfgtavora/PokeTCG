package com.lfgtavora.poketcg.search.impl.preview

import com.lfgtavora.poketcg.model.data.SearchResultItem

internal val fakeSearchResults = listOf(
    SearchResultItem.Card(
        id = "swsh1-25",
        name = "Pikachu",
        image = "https://assets.tcgdex.net/en/swsh/swsh1/25/low.webp",
        set = SearchResultItem.Set(
            id = "swsh1",
            name = "Sword & Shield",
            logo = "https://assets.tcgdex.net/en/swsh/swsh1/logo.webp",
            series = "Sword & Shield",
        ),
    ),
    SearchResultItem.Set(
        id = "swsh1",
        name = "Sword & Shield",
        logo = "https://assets.tcgdex.net/en/swsh/swsh1/logo.webp",
        series = "Sword & Shield",
    ),
    SearchResultItem.Card(
        id = "swsh3-20",
        name = "Charizard",
        image = "https://assets.tcgdex.net/en/swsh/swsh3/20/low.webp",
        set = SearchResultItem.Set(
            id = "swsh3",
            name = "Darkness Ablaze",
            logo = "https://assets.tcgdex.net/en/swsh/swsh3/logo.webp",
            series = "Sword & Shield",
        ),
    ),
)