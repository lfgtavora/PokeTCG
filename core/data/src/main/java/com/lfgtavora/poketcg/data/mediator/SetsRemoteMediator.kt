package com.lfgtavora.poketcg.data.mediator

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import com.lfgtavora.poketcg.data.mapper.asEntity
import com.lfgtavora.poketcg.database.dao.SetDao
import com.lfgtavora.poketcg.database.dao.SetRemoteKeyDao
import com.lfgtavora.poketcg.database.model.SetEntity
import com.lfgtavora.poketcg.database.model.SetRemoteKeysEntity
import com.lfgtavora.poketcg.network.model.SetDataListResponse

@OptIn(ExperimentalPagingApi::class)
internal class SetsRemoteMediator(
    private val setRemoteKeyDao: SetRemoteKeyDao,
    private val setDao: SetDao,
    private val fetchNetWorkData: suspend (page: Int, pageSize: Int) -> Result<SetDataListResponse>,
    private val transactionRunner: TransactionRunner,
) : RemoteMediator<Int, SetEntity>() {

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, SetEntity>
    ): MediatorResult {

        val page = when (loadType) {
            LoadType.REFRESH -> {
                val remoteKeys = getRemoteKeyClosestToCurrentPosition(state)
                remoteKeys?.nextKey?.minus(1) ?: 1
            }

            LoadType.PREPEND -> {
                val remoteKeys = getRemoteKeyForFirstItem(state)
                val prevKey = remoteKeys?.prevKey
                    ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                prevKey
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
            return MediatorResult.Error(it)
        }

        val endOfPaginationReached =
            fetchNetWorkDataResult.count >= fetchNetWorkDataResult.totalCount

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

    private suspend fun getRemoteKeyForFirstItem(state: PagingState<Int, SetEntity>): SetRemoteKeysEntity? {
        return state.pages.firstOrNull { it.data.isNotEmpty() }?.data?.firstOrNull()
            ?.let { set ->
                setRemoteKeyDao.remoteKeysSetId(set.id)
            }
    }

    private suspend fun getRemoteKeyClosestToCurrentPosition(
        state: PagingState<Int, SetEntity>
    ): SetRemoteKeysEntity? {
        return state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.id?.let { setId ->
                setRemoteKeyDao.remoteKeysSetId(setId)
            }
        }
    }
}
