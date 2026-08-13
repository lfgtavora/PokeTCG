package com.lfgtavora.poketcg.feature.home.api

import androidx.paging.PagingData
import com.lfgtavora.poketcg.model.data.SetPreview
import kotlinx.coroutines.flow.Flow

interface ObservePaginatedSetsPreviewByLastReleaseDateUseCase {
    operator fun invoke(): Flow<PagingData<SetPreview>>
}
