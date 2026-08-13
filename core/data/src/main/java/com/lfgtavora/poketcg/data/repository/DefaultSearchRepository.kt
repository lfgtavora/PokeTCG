package com.lfgtavora.poketcg.data.repository

import com.lfgtavora.poketcg.core.common.suspendRunCatching
import com.lfgtavora.poketcg.core.crashlytics.CrashlyticsHelper
import com.lfgtavora.poketcg.core.crashlytics.recordException
import com.lfgtavora.poketcg.data.di.IoDispatcher
import com.lfgtavora.poketcg.data.mapper.asSearchResultItem
import com.lfgtavora.poketcg.model.data.SearchResultItem
import com.lfgtavora.poketcg.network.TcgDexNetworkDataSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class DefaultSearchRepository @Inject constructor(
    private val network: TcgDexNetworkDataSource,
    private val crashlytics: CrashlyticsHelper,
    @param:IoDispatcher private val ioDispatcher: CoroutineDispatcher,
) : SearchRepository {

    override suspend fun search(
        query: String,
        types: String,
    ): Result<List<SearchResultItem>> =
        withContext(ioDispatcher) {
            suspendRunCatching {
                network.search(query = query, types = types)
                    .data
                    .map { it.asSearchResultItem() }
            }.onFailure { throwable ->
                crashlytics.recordException(
                    throwable = throwable,
                    extras = mapOf("query" to query)
                )
            }
        }
}
