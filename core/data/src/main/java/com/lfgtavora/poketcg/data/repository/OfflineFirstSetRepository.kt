package com.lfgtavora.poketcg.data.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import androidx.room.withTransaction
import com.lfgtavora.poketcg.core.common.RetryPolicy
import com.lfgtavora.poketcg.core.common.suspendRunCatching
import com.lfgtavora.poketcg.core.common.withRetry
import com.lfgtavora.poketcg.core.crashlytics.CrashlyticsHelper
import com.lfgtavora.poketcg.core.crashlytics.recordException
import com.lfgtavora.poketcg.data.di.IoDispatcher
import com.lfgtavora.poketcg.data.mapper.asEntity
import com.lfgtavora.poketcg.data.mediator.SetsRemoteMediator
import com.lfgtavora.poketcg.database.PokeTcgDatabase
import com.lfgtavora.poketcg.database.dao.SetDao
import com.lfgtavora.poketcg.database.dao.SetRemoteKeyDao
import com.lfgtavora.poketcg.database.model.SetEntity
import com.lfgtavora.poketcg.database.model.asModel
import com.lfgtavora.poketcg.database.model.asPreviewModel
import com.lfgtavora.poketcg.model.data.SetModel
import com.lfgtavora.poketcg.model.data.SetPreview
import com.lfgtavora.poketcg.network.TcgDexNetworkDataSource
import com.lfgtavora.poketcg.network.model.SetDataListResponse
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.io.IOException
import javax.inject.Inject

class OfflineFirstSetRepository @Inject constructor(
    private val network: TcgDexNetworkDataSource,
    private val setDao: SetDao,
    private val setRemoteKeyDao: SetRemoteKeyDao,
    private val database: PokeTcgDatabase,
    private val crashlytics: CrashlyticsHelper,
    @param:IoDispatcher private val ioDispatcher: CoroutineDispatcher,
) : SetRepository {

    @OptIn(ExperimentalPagingApi::class)
    override fun observeSetsPagingData(
        page: Int,
        pageSize: Int,
        orderBy: String?,
        field: String?
    ): Flow<PagingData<SetPreview>> {

        return Pager(
            config = PagingConfig(
                pageSize = pageSize,
                enablePlaceholders = true
            ),
            remoteMediator = SetsRemoteMediator(
                setRemoteKeyDao = setRemoteKeyDao,
                setDao = setDao,
                fetchNetWorkData = { pageToFetch: Int, pageSize: Int ->
                    getSetsList(
                        page = pageToFetch,
                        pageSize = pageSize,
                        orderBy = orderBy,
                        field = field
                    )
                },
                transactionRunner = { block ->
                    database.withTransaction { block() }
                },
            ),
            pagingSourceFactory = { setDao.pagingSource() }
        ).flow.map { pagingData ->
            pagingData.map(SetEntity::asPreviewModel)
        }
    }

    internal suspend fun getSetsList(
        page: Int,
        pageSize: Int,
        orderBy: String? = null,
        field: String? = null
    ): Result<SetDataListResponse> = withContext(ioDispatcher) {
        suspendRunCatching {
            val setsPagingData = withRetry {
                network.getSets(
                    page = page,
                    pageSize = pageSize,
                    orderBy = orderBy,
                    field = field
                )
            }
            setsPagingData
        }
    }

    override fun observeSet(id: String): Flow<SetModel> {
        return setDao
            .getById(id)
            .map { it.asModel() }
            .flowOn(ioDispatcher)
    }

    override suspend fun syncSet(
        id: String,
        retryPolicy: RetryPolicy
    ): Result<Unit> =
        withContext(ioDispatcher) {
            suspendRunCatching {
                val setResponse = withRetry(retryPolicy) { network.getSet(id) }
                setDao.insert(setResponse.asEntity())
            }.onFailure { throwable ->
                if (throwable !is IOException) {
                    crashlytics.recordException(
                        throwable = throwable,
                        extras = mapOf(
                            "set_id" to id,
                            "retry_police" to retryPolicy.toString()
                        ),
                    )
                }
            }
        }
}
