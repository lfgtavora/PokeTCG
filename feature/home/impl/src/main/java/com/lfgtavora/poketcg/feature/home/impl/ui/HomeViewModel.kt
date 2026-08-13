package com.lfgtavora.poketcg.feature.home.impl.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.lfgtavora.poketcg.feature.home.api.ObservePaginatedSetsPreviewByLastReleaseDateUseCase
import com.lfgtavora.poketcg.model.data.SetPreview
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    observePaginatedSetsPreviewByLastReleaseDateUseCase: ObservePaginatedSetsPreviewByLastReleaseDateUseCase
) : ViewModel() {

    val setsPagingData: Flow<PagingData<SetPreview>> =
        observePaginatedSetsPreviewByLastReleaseDateUseCase()
            .cachedIn(viewModelScope)
}
