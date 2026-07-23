package com.lfgtavora.poketcg.feature.home.impl

import com.lfgtavora.poketcg.model.data.SetPreview

sealed interface HomeUiState {
    object Loading : HomeUiState
    data class Success(val sets: List<SetPreview>) : HomeUiState
    data object Error : HomeUiState
}