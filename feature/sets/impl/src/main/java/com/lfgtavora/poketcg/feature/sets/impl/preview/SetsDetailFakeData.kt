package com.lfgtavora.poketcg.feature.sets.impl.preview

import com.lfgtavora.poketcg.model.data.CardPreview
import com.lfgtavora.poketcg.model.data.CardPreviewImage
import com.lfgtavora.poketcg.model.data.SetPreview

internal val fakeSet = SetPreview(
    id = "base1",
    name = "Base Set",
    releaseDate = "1999-01-09",
    logo = "https://images.pokemontcg.io/base1/logo.png",
    totalCards = 102,
)

internal val fakeCardPreview = CardPreview(
    id = "base1-4",
    name = "Charizard",
    image = CardPreviewImage(
        small = "https://images.pokemontcg.io/base1/4.png",
        large = "https://images.pokemontcg.io/base1/4_hires.png",
    ),
    setId = "base1",
)

internal val fakeCardPreviewList = listOf(
    fakeCardPreview,
    CardPreview(
        id = "base1-1",
        name = "Alakazam",
        image = CardPreviewImage(
            small = "https://images.pokemontcg.io/base1/1.png",
            large = "https://images.pokemontcg.io/base1/1_hires.png",
        ),
        setId = "base1",
    ),
    CardPreview(
        id = "base1-2",
        name = "Blastoise",
        image = CardPreviewImage(
            small = "https://images.pokemontcg.io/base1/2.png",
            large = "https://images.pokemontcg.io/base1/2_hires.png",
        ),
        setId = "base1",
    ),
    CardPreview(
        id = "base1-3",
        name = "Chansey",
        image = CardPreviewImage(
            small = "https://images.pokemontcg.io/base1/3.png",
            large = "https://images.pokemontcg.io/base1/3_hires.png",
        ),
        setId = "base1",
    ),
    CardPreview(
        id = "base1-5",
        name = "Clefairy",
        image = CardPreviewImage(
            small = "https://images.pokemontcg.io/base1/5.png",
            large = "https://images.pokemontcg.io/base1/5_hires.png",
        ),
        setId = "base1",
    ),
    CardPreview(
        id = "base1-6",
        name = "Gyarados",
        image = CardPreviewImage(
            small = "https://images.pokemontcg.io/base1/6.png",
            large = "https://images.pokemontcg.io/base1/6_hires.png",
        ),
        setId = "base1",
    ),
    CardPreview(
        id = "base1-7",
        name = "Hitmonchan",
        image = CardPreviewImage(
            small = "https://images.pokemontcg.io/base1/7.png",
            large = "https://images.pokemontcg.io/base1/7_hires.png",
        ),
        setId = "base1",
    ),
    CardPreview(
        id = "base1-8",
        name = "Machamp",
        image = CardPreviewImage(
            small = "https://images.pokemontcg.io/base1/8.png",
            large = "https://images.pokemontcg.io/base1/8_hires.png",
        ),
        setId = "base1",
    ),
)
