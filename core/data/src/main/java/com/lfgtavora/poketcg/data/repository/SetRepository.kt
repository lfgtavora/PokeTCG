package com.lfgtavora.poketcg.data.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.PagingData
import com.lfgtavora.poketcg.core.common.RetryPolicy
import com.lfgtavora.poketcg.model.data.SetModel
import com.lfgtavora.poketcg.model.data.SetPreview
import kotlinx.coroutines.flow.Flow

interface SetRepository {
    fun observeSet(id: String): Flow<SetModel>
    @OptIn(ExperimentalPagingApi::class)
     fun observeSetsPagingData(
        page: Int,
        pageSize: Int,
        orderBy: String? = null,
        field: String? = null
    ): Flow<PagingData<SetPreview>>

    suspend fun syncSet(id: String, retryPolicy: RetryPolicy = RetryPolicy.Default): Result<Unit>
}
