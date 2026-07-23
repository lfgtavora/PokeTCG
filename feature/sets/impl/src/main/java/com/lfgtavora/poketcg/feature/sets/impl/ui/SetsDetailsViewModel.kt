package com.lfgtavora.poketcg.feature.sets.impl.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.lfgtavora.poketcg.data.repository.SetRepository
import com.lfgtavora.poketcg.feature.sets.impl.domain.GetCardsPreviewFromSetUseCase
import com.lfgtavora.poketcg.model.data.CardPreview
import com.lfgtavora.poketcg.model.data.SetPreview
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

@HiltViewModel(assistedFactory = SetsDetailsViewModel.Factory::class)
class SetsDetailsViewModel @AssistedInject constructor(
    @Assisted val setId: String,
    setRepository: SetRepository,
    getCardsPreviewFromSetUseCase: GetCardsPreviewFromSetUseCase
) : ViewModel() {

    val setUiState: StateFlow<SetUiState> =
        setRepository.getSet(setId)
            .map<SetPreview, SetUiState> {
                SetUiState.Success(it)
            }.catch {
                emit(SetUiState.Error)
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = SetUiState.Loading
            )

    val cardsPagingData: Flow<PagingData<CardPreview>> =
        getCardsPreviewFromSetUseCase(setId).cachedIn(viewModelScope)


    @AssistedFactory
    interface Factory {
        fun create(
            setId: String,
        ): SetsDetailsViewModel
    }

}

sealed interface SetUiState {
    object Loading : SetUiState
    object Error : SetUiState
    data class Success(
        val set: SetPreview,
    ) : SetUiState
}