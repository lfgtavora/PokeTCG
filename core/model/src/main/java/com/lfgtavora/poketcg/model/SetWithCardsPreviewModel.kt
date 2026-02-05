package com.lfgtavora.poketcg.model

data class SetWithCardsPreviewModel(
    val set: SetModel,
    val cardsPreview: List<CardPreview>
)