package com.lfgtavora.poketcg.data.repository

import androidx.paging.PagingData
import com.lfgtavora.poketcg.core.common.RetryPolicy
import com.lfgtavora.poketcg.model.data.Card
import com.lfgtavora.poketcg.model.data.CardLookup
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

    fun observeCard(id: String): Flow<CardLookup?>

    suspend fun syncCard(
        id: String,
        retryPolicy: RetryPolicy = RetryPolicy.Default
    ): Result<Unit>
}
