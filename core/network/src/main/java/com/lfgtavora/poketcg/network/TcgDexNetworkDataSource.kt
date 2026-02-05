package com.lfgtavora.poketcg.network

import com.lfgtavora.poketcg.network.model.CardBriefResponse
import com.lfgtavora.poketcg.network.model.CardDataListResponse
import com.lfgtavora.poketcg.network.model.CardResponse
import com.lfgtavora.poketcg.network.model.SetResponse

interface TcgDexNetworkDataSource {
    suspend fun getAllSets(): List<SetResponse>

    suspend fun getSetsBrief(
        page: Int,
        pageSize: Int,
        orderBy: String? = null,
        field: String? = null,
    ): List<SetResponse>

    suspend fun getSet(id: String): SetResponse
    suspend fun getCard(id: String): CardBriefResponse

    suspend fun getCards(
        query: String,
        page: Int,
        pageSize: Int,
        select: String? = null,
    ): CardDataListResponse

}