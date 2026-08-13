package com.lfgtavora.poketcg.feature.home.impl.domain

import androidx.paging.PagingData
import com.lfgtavora.poketcg.data.repository.SetRepository
import com.lfgtavora.poketcg.feature.home.api.ObservePaginatedSetsPreviewByLastReleaseDateUseCase
import com.lfgtavora.poketcg.model.data.SetPreview
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow

class ObservePaginatedSetsPreviewByLastReleaseDateUseCaseImpl @Inject constructor(
    private val setRepository: SetRepository,
) : ObservePaginatedSetsPreviewByLastReleaseDateUseCase {

    override operator fun invoke(): Flow<PagingData<SetPreview>> =
        setRepository.observeSetsPagingData(
            page = 1,
            pageSize = 16,
            orderBy = "-releaseDate",
        )

}
