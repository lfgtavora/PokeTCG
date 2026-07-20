package com.lfgtavora.poketcg.data.repository

import androidx.paging.PagingData
import com.lfgtavora.poketcg.database.model.CardEntity
import com.lfgtavora.poketcg.model.CardPreview
import kotlinx.coroutines.flow.Flow

interface CardRepository {

    fun getCardsFromSet(
        setId: String,
        pageSize: Int,
        query: String,
        select: String
    ): Flow<PagingData<CardPreview>>

    fun getCard(id: String): Flow<CardEntity?>
}
