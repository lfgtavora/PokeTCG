package com.lfgtavora.poketcg.search.impl

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lfgtavora.poketcg.data.repository.SearchRepository
import com.lfgtavora.poketcg.model.data.SearchResultItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchRepository: SearchRepository
) : ViewModel() {

    private val _query = MutableStateFlow("")

    private val _searchResultState = _query
        .debounce(300L)
        .distinctUntilChanged()
        .flatMapLatest { query ->
            flow {
                if (query.length < 3) {
                    emit(SearchResult.Idle)
                    return@flow
                }

                emit(SearchResult.Loading)
                try {
                    val results = searchRepository.search(query)
                    emit(SearchResult.Success(results))
                } catch (e: Exception) {
                    emit(SearchResult.Error(e.localizedMessage ?: "Erro desconhecido"))
                }
            }
        }

    val uiState: StateFlow<SearchUiState> = combine(
        _query,
        _searchResultState
    ) { query, result ->
        when (result) {
            is SearchResult.Idle -> SearchUiState(query = query)
            is SearchResult.Loading -> SearchUiState(query = query, isLoading = true)
            is SearchResult.Success -> SearchUiState(query = query, items = result.data)
            is SearchResult.Error -> SearchUiState(query = query, error = result.message)
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = SearchUiState()
    )

    fun onQueryChange(newQuery: String) {
        _query.value = newQuery
    }

    fun onClearQuery() {
        _query.value = ""
    }
}

sealed interface SearchResult {
    object Idle : SearchResult
    object Loading : SearchResult
    data class Success(val data: List<SearchResultItem>) : SearchResult
    data class Error(val message: String) : SearchResult
}

data class SearchUiState(
    val query: String = "",
    val isLoading: Boolean = false,
    val items: List<SearchResultItem> = emptyList(),
    val error: String? = null
)
