package com.lfgtavora.poketcg.data.repository

import com.lfgtavora.poketcg.data.model.SetDomain
import com.lfgtavora.poketcg.model.SetData
import com.lfgtavora.poketcg.network.model.SetResponse
import kotlinx.coroutines.flow.Flow

interface SetRepository {
    fun getAllSetsPaginated(
        page: Int,
        itemsPerPage: Int,
        orderBy: String? = null,
        field: String? = null,
    ): Flow<List<SetData>>

    fun getSet(id: String): Flow<SetData?>

}