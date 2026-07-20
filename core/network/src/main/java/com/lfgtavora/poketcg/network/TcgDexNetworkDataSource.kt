package com.lfgtavora.poketcg.network

import com.lfgtavora.poketcg.network.model.CardDataListResponse
import com.lfgtavora.poketcg.network.model.CardDataResponse
import com.lfgtavora.poketcg.network.model.SearchDataResponse
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
    suspend fun getCard(id: String): CardDataResponse

    suspend fun getCards(
        query: String,
        page: Int,
        pageSize: Int,
        select: String? = null,
    ): CardDataListResponse

    suspend fun search(
        query: String,
        types: String = "card,set",
    ): SearchDataResponse
}