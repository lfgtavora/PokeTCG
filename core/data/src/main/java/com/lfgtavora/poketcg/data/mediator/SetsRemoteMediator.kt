package com.lfgtavora.poketcg.data.mediator

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import com.lfgtavora.poketcg.core.crashlytics.CrashlyticsHelper
import com.lfgtavora.poketcg.core.crashlytics.recordException
import com.lfgtavora.poketcg.data.mapper.asEntity
import com.lfgtavora.poketcg.database.dao.SetDao
import com.lfgtavora.poketcg.database.dao.SetRemoteKeyDao
import com.lfgtavora.poketcg.database.model.SetEntity
import com.lfgtavora.poketcg.database.model.SetRemoteKeysEntity
import com.lfgtavora.poketcg.network.model.SetDataListResponse
import java.util.concurrent.atomic.AtomicBoolean

@OptIn(ExperimentalPagingApi::class)
internal class SetsRemoteMediator(
    private val setRemoteKeyDao: SetRemoteKeyDao,
    private val setDao: SetDao,
    private val fetchNetWorkData: suspend (page: Int, pageSize: Int) -> Result<SetDataListResponse>,
    private val transactionRunner: TransactionRunner,
    private val crashlytics: CrashlyticsHelper,
) : RemoteMediator<Int, SetEntity>() {

    private val initialized = AtomicBoolean(false)

    override suspend fun initialize(): InitializeAction {
        return if (initialized.compareAndSet(false, true)) {
            InitializeAction.LAUNCH_INITIAL_REFRESH
        } else {
            InitializeAction.SKIP_INITIAL_REFRESH
        }
    }

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, SetEntity>
    ): MediatorResult {

        val page = when (loadType) {
            LoadType.REFRESH -> 1
            // Newest-first feed: nothing exists before page 1. New sets arrive via REFRESH.
            LoadType.PREPEND -> {
                return MediatorResult.Success(endOfPaginationReached = true)
            }
            LoadType.APPEND -> {
                val remoteKeys = getRemoteKeyForLastItem(state)
                val nextKey = remoteKeys?.nextKey
                    ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                nextKey
            }
        }

        val fetchNetWorkDataResult = fetchNetWorkData(
            page,
            state.config.pageSize
        ).getOrElse {
            crashlytics.recordException(
                throwable = it,
                extras = mapOf(
                    "source" to "home_sets_pagination",
                    "page" to page.toString(),
                    "pageSize" to state.config.pageSize.toString(),
                ),
            )
            return MediatorResult.Error(it)
        }

        val pageSize = state.config.pageSize
        val endOfPaginationReached = fetchNetWorkDataResult.data.isEmpty() ||
            fetchNetWorkDataResult.data.size < pageSize ||
            page * pageSize >= fetchNetWorkDataResult.totalCount

        val prevKey = if (page == 1) null else page - 1
        val nextKey = if (endOfPaginationReached) null else page + 1
        val keys = fetchNetWorkDataResult.data.map {
            SetRemoteKeysEntity(setId = it.id, prevKey = prevKey, nextKey = nextKey)
        }

        transactionRunner {
            setRemoteKeyDao.insertAll(keys)
            setDao.insertMany(
                fetchNetWorkDataResult.data.map { it.asEntity() }
            )
        }

        return MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
    }

    private suspend fun getRemoteKeyForLastItem(state: PagingState<Int, SetEntity>): SetRemoteKeysEntity? {
        return state.pages.lastOrNull { it.data.isNotEmpty() }?.data?.lastOrNull()
            ?.let { set ->
                setRemoteKeyDao.remoteKeysSetId(set.id)
            }
    }
}
