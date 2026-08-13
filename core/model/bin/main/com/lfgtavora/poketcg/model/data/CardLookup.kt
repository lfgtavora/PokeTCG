package com.lfgtavora.poketcg.model.data

sealed interface CardLookup {
    data class Partial(val card: CardPreview) : CardLookup
    data class Full(val card: Card) : CardLookup
}
