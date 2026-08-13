package com.lfgtavora.poketcg.feature.card_detail.impl.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lfgtavora.poketcg.core.common.SyncState
import com.lfgtavora.poketcg.data.repository.CardRepository
import com.lfgtavora.poketcg.model.data.Card
import com.lfgtavora.poketcg.model.data.CardLookup
import com.lfgtavora.poketcg.model.data.CardPreview
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel(assistedFactory = CardDetailViewModel.Factory::class)
class CardDetailViewModel @AssistedInject constructor(
    @Assisted val cardId: String,
    private val cardRepository: CardRepository,
) : ViewModel() {

    private val syncState = MutableStateFlow(SyncState.Syncing)

    init {
        syncData()
    }

    val uiState: StateFlow<CardDetailUiState> = combine(
        cardRepository.observeCard(cardId),
        syncState,
    ) { lookup, syncState ->
        val content = when {
            lookup is CardLookup.Full -> Content.Full(lookup.card)
            lookup is CardLookup.Partial -> Content.Partial(lookup.card)
            syncState == SyncState.Error -> Content.Error
            else -> Content.Loading
        }
        CardDetailUiState(content = content, syncState = syncState)
    }
        .catch { emit(CardDetailUiState(Content.Error, SyncState.Error)) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = CardDetailUiState(Content.Loading, SyncState.Syncing),
        )

    private fun syncData() {
        viewModelScope.launch {
            syncState.value = SyncState.Syncing
            cardRepository.syncCard(cardId)
                .onSuccess { syncState.value = SyncState.Success }
                .onFailure { syncState.value = SyncState.Error }
        }
    }

    internal fun retry() =
        syncData()

    @AssistedFactory
    interface Factory {
        fun create(
            cardId: String,
        ): CardDetailViewModel
    }

}

data class CardDetailUiState(
    val content: Content,
    val syncState: SyncState,
) {
    val isSyncing: Boolean
        get() = syncState == SyncState.Syncing
}

sealed interface Content {
    data object Loading : Content
    data class Partial(val card: CardPreview) : Content
    data class Full(val card: Card) : Content
    data object NotFound : Content
    data object Error : Content
}
