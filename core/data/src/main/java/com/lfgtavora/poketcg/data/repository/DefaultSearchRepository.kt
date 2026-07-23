package com.lfgtavora.poketcg.data.repository

import com.lfgtavora.poketcg.data.di.IoDispatcher
import com.lfgtavora.poketcg.data.mapper.asSearchResultItem
import com.lfgtavora.poketcg.model.data.SearchResultItem
import com.lfgtavora.poketcg.network.TcgDexNetworkDataSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class DefaultSearchRepository @Inject constructor(
    private val network: TcgDexNetworkDataSource,
    @param:IoDispatcher private val ioDispatcher: CoroutineDispatcher,
) : SearchRepository {

    override suspend fun search(
        query: String,
        types: String,
    ): List<SearchResultItem> =
        withContext(ioDispatcher) {
            network.search(query = query, types = types)
                .data
                .map { it.asSearchResultItem() }
        }
}
