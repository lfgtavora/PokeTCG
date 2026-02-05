package com.lfgtavora.poketcg.data.mapper

import com.lfgtavora.poketcg.database.model.SetEntity
import com.lfgtavora.poketcg.network.model.SetResponse

fun SetResponse.asEntity(): SetEntity =
    SetEntity(
        id = id,
        name = name.orEmpty(),
        series = series,
        printedTotal = printedTotal ?: 0,
        total = total ?: 0,
        ptcgoCode = ptcgoCode,
        releaseDate = releaseDate.orEmpty(),
        updatedAt = updatedAt,
        symbol = images?.symbol,
        logo = images?.logo,
        legalities = SetEntity.Legalities(
            standard = legalitiesResponse?.standard,
            expanded = legalitiesResponse?.expanded,
            unlimited = legalitiesResponse?.unlimited
        )
    )
