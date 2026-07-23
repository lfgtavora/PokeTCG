package com.lfgtavora.poketcg.feature.home.impl

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.lfgtavora.poketcg.data.repository.SetRepository
import com.lfgtavora.poketcg.model.data.SetPreview
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    setRepository: SetRepository
) : ViewModel() {

    val setsPagingData: Flow<PagingData<SetPreview>> =
        setRepository.getSets()
            .cachedIn(viewModelScope)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = PagingData.empty()
            )

}
