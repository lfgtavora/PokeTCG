package com.lfgtavora.poketcg.feature.card_detail.impl

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lfgtavora.poketcg.data.repository.CardRepository
import com.lfgtavora.poketcg.database.model.CardEntity
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

@HiltViewModel(assistedFactory = CardDetailViewModel.Factory::class)
class CardDetailViewModel @AssistedInject constructor(
    @Assisted val cardId: String,
    cardRepository: CardRepository,
) : ViewModel() {

    val uiState: StateFlow<CardDetailUiState> = cardRepository.getCard(
        id = cardId
    ).map<CardEntity?, CardDetailUiState> {
        CardDetailUiState.Success(it)
    }.catch {
        emit(CardDetailUiState.Error)
    }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = CardDetailUiState.Loading
        )


    @AssistedFactory
    interface Factory {
        fun create(
            cardId: String,
        ): CardDetailViewModel
    }

}

sealed interface CardDetailUiState {
    data class Success(val card: CardEntity?) : CardDetailUiState
    object Error : CardDetailUiState
    object Loading : CardDetailUiState
}
