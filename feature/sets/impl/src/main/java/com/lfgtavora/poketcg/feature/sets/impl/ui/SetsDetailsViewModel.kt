package com.lfgtavora.poketcg.feature.sets.impl.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.lfgtavora.poketcg.core.analytics.AnalyticsEvent
import com.lfgtavora.poketcg.core.analytics.AnalyticsHelper
import com.lfgtavora.poketcg.core.common.SyncState
import com.lfgtavora.poketcg.data.repository.SetRepository
import com.lfgtavora.poketcg.feature.sets.impl.domain.GetCardsPreviewFromSetUseCase
import com.lfgtavora.poketcg.model.data.CardPreview
import com.lfgtavora.poketcg.model.data.SetModel
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel(assistedFactory = SetsDetailsViewModel.Factory::class)
class SetsDetailsViewModel @AssistedInject constructor(
    @Assisted val setId: String,
    val setRepository: SetRepository,
    getCardsPreviewFromSetUseCase: GetCardsPreviewFromSetUseCase,
    private val analyticsHelper: AnalyticsHelper,
) : ViewModel() {

    private val syncState = MutableStateFlow(SyncState.Syncing)
    private val showSetInfo = MutableStateFlow(false)

    init {
        syncData()
    }

    private fun syncData() {
        viewModelScope.launch {
            syncState.value = SyncState.Syncing
            setRepository.syncSet(setId)
                .onSuccess { syncState.value = SyncState.Success }
                .onFailure { syncState.value = SyncState.Error }
        }
    }

    val uiState: StateFlow<SetDetailUiState> = combine(
        setRepository.observeSet(setId)
            .map<SetModel, SetUiState> { SetUiState.Success(it) }
            .catch { emit(SetUiState.Error) },
        showSetInfo,
    ) { setState, showSetInfo ->
        SetDetailUiState(
            setState = setState,
            showSetInfo = showSetInfo,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = SetDetailUiState(),
    )

    val cardsPagingData: Flow<PagingData<CardPreview>> =
        getCardsPreviewFromSetUseCase(setId).cachedIn(viewModelScope)

    fun onSetInfoClicked() {
        analyticsHelper.logSetInfoClicked(setId)
        showSetInfo.value = true
    }

    fun onSetInfoDismissed() {
        showSetInfo.value = false
    }

    @AssistedFactory
    interface Factory {
        fun create(
            setId: String,
        ): SetsDetailsViewModel
    }

}

data class SetDetailUiState(
    val setState: SetUiState = SetUiState.Loading,
    val showSetInfo: Boolean = false,
)

sealed interface SetUiState {
    object Loading : SetUiState
    object Error : SetUiState
    data class Success(
        val set: SetModel,
    ) : SetUiState
}

private fun AnalyticsHelper.logSetInfoClicked(setId: String) {
    logEvent(
        AnalyticsEvent(
            type = "set_info_clicked",
            extras = listOf(
                AnalyticsEvent.Param("set_id", setId),
            ),
        ),
    )
}