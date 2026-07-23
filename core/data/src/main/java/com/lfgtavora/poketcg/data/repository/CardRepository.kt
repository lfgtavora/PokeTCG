package com.lfgtavora.poketcg.data.repository

import androidx.paging.PagingData
import com.lfgtavora.poketcg.model.data.Card
import com.lfgtavora.poketcg.model.data.CardPreview
import kotlinx.coroutines.flow.Flow

interface CardRepository {

    fun getCardsFromSet(
        setId: String,
        pageSize: Int,
        query: String,
        select: String,
        orderBy: String? = null
    ): Flow<PagingData<CardPreview>>

    fun getCard(id: String): Flow<Card?>
}
