package com.lfgtavora.poketcg.search.impl

import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.lfgtavora.poketcg.data.repository.SearchRepository
import com.lfgtavora.poketcg.model.data.SearchResultItem
import com.lfgtavora.poketcg.search.impl.ui.SearchViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SearchViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var repository: FakeSearchRepository
    private lateinit var viewModel: SearchViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        repository = FakeSearchRepository()
        viewModel = SearchViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `query shorter than 3 stays idle and does not call repo`() = runTest(testDispatcher) {
        viewModel.uiState.test {
            skipItems(1) // initial
            viewModel.onQueryChange("ab")
            advanceTimeBy(300)
            advanceUntilIdle()

            val state = expectMostRecentItem()
            assertThat(state.query).isEqualTo("ab")
            assertThat(state.isLoading).isFalse()
            assertThat(state.items).isEmpty()
            assertThat(state.error).isNull()
            assertThat(repository.queries).isEmpty()
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `query of 3 chars loads then succeeds`() = runTest(testDispatcher) {
        val results = listOf(
            SearchResultItem.Set(id = "sv1", name = "Scarlet", logo = null, series = null)
        )
        repository.results = results

        viewModel.uiState.test {
            skipItems(1)
            viewModel.onQueryChange("pik")
            advanceTimeBy(300)
            advanceUntilIdle()

            val state = expectMostRecentItem()
            assertThat(state.isLoading).isFalse()
            assertThat(state.items).isEqualTo(results)
            assertThat(repository.queries).containsExactly("pik")
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `repo throw maps to error with fallback message`() = runTest(testDispatcher) {
        repository.throwable = RuntimeException()

        viewModel.uiState.test {
            skipItems(1)
            viewModel.onQueryChange("abc")
            advanceTimeBy(300)
            advanceUntilIdle()

            val state = expectMostRecentItem()
            assertThat(state.error).isEqualTo("Erro desconhecido")
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `repo throw uses localizedMessage when present`() = runTest(testDispatcher) {
        repository.throwable = RuntimeException("boom")

        viewModel.uiState.test {
            skipItems(1)
            viewModel.onQueryChange("abc")
            advanceTimeBy(300)
            advanceUntilIdle()

            assertThat(expectMostRecentItem().error).isEqualTo("boom")
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `rapid typing only searches last distinct query after debounce`() = runTest(testDispatcher) {
        repository.results = emptyList()

        viewModel.uiState.test {
            skipItems(1)
            viewModel.onQueryChange("pik")
            advanceTimeBy(100)
            viewModel.onQueryChange("pika")
            advanceTimeBy(300)
            advanceUntilIdle()

            assertThat(repository.queries).containsExactly("pika")
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `same query twice after debounce calls repo once`() = runTest(testDispatcher) {
        repository.results = emptyList()

        viewModel.uiState.test {
            skipItems(1)
            viewModel.onQueryChange("pik")
            advanceTimeBy(300)
            advanceUntilIdle()
            viewModel.onQueryChange("pik")
            advanceTimeBy(300)
            advanceUntilIdle()

            assertThat(repository.queries).containsExactly("pik")
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onClearQuery resets to empty idle`() = runTest(testDispatcher) {
        repository.results = emptyList()

        viewModel.uiState.test {
            skipItems(1)
            viewModel.onQueryChange("pik")
            advanceTimeBy(300)
            advanceUntilIdle()
            viewModel.onClearQuery()
            advanceTimeBy(300)
            advanceUntilIdle()

            val state = expectMostRecentItem()
            assertThat(state.query).isEmpty()
            assertThat(state.items).isEmpty()
            assertThat(state.isLoading).isFalse()
            assertThat(state.error).isNull()
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `mid flight query change keeps only latest result`() = runTest(testDispatcher) {
        repository.resultsByQuery = mapOf(
            "aaa" to listOf(SearchResultItem.Set("a", "A", null, null)),
            "bbb" to listOf(SearchResultItem.Set("b", "B", null, null)),
        )

        viewModel.uiState.test {
            skipItems(1)
            viewModel.onQueryChange("aaa")
            advanceTimeBy(300)
            viewModel.onQueryChange("bbb")
            advanceTimeBy(300)
            advanceUntilIdle()

            val state = expectMostRecentItem()
            assertThat(state.query).isEqualTo("bbb")
            assertThat(state.items.single().let { (it as SearchResultItem.Set).id }).isEqualTo("b")
            assertThat(repository.queries.last()).isEqualTo("bbb")
            cancelAndIgnoreRemainingEvents()
        }
    }

    private class FakeSearchRepository : SearchRepository {
        var results: List<SearchResultItem> = emptyList()
        var resultsByQuery: Map<String, List<SearchResultItem>> = emptyMap()
        var throwable: Throwable? = null
        val queries = mutableListOf<String>()

        override suspend fun search(query: String, types: String): List<SearchResultItem> {
            queries += query
            throwable?.let { throw it }
            return resultsByQuery[query] ?: results
        }
    }
}
