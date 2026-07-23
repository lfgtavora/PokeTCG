package com.lfgtavora.poketcg.data.repository

import androidx.paging.PagingData
import com.lfgtavora.poketcg.model.data.SetPreview
import kotlinx.coroutines.flow.Flow

interface SetRepository {
    fun getSets(): Flow<PagingData<SetPreview>>

    fun getSet(id: String): Flow<SetPreview>
}
