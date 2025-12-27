package com.lfgtavora.poketcg.network

import com.lfgtavora.poketcg.network.model.SetBriefResponse
import com.lfgtavora.poketcg.network.model.SetResponse

interface TcgDexNetworkDataSource {
    suspend fun getAllSets(): List<SetBriefResponse>

    suspend fun getSets(
        page: Int,
        itemsPerPage: Int,
        orderBy: String? = null,
        field: String? = null,
    ): List<SetBriefResponse?>?
    suspend fun getSet(id: String): SetResponse
    suspend fun getCard(id: String): Any
}