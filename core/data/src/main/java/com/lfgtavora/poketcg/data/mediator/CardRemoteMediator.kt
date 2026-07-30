package com.lfgtavora.poketcg.data.mediator

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import com.lfgtavora.poketcg.data.mapper.asEntity
import com.lfgtavora.poketcg.database.dao.CardDao
import com.lfgtavora.poketcg.database.dao.CardRemoteKeyDao
import com.lfgtavora.poketcg.database.model.CardEntity
import com.lfgtavora.poketcg.database.model.CardRemoteKeysEntity
import com.lfgtavora.poketcg.network.TcgDexNetworkDataSource
import com.lfgtavora.poketcg.network.model.CardResponse
import java.io.IOException

@OptIn(ExperimentalPagingApi::class)
internal class CardsRemoteMediator(
    private val setId: String,
    private val cardDao: CardDao,
    private val cardRemoteKeyDao: CardRemoteKeyDao,
    private val network: TcgDexNetworkDataSource,
    private val query: String,
    private val select: String,
    private val orderBy: String? = null,
    private val transactionRunner: TransactionRunner,
) : RemoteMediator<Int, CardEntity>() {

    override suspend fun initialize(): InitializeAction {
        return cardsInitializeAction(cardDao.getCardsCountBySet(setId))
    }

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, CardEntity>
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

        try {
            val response = network.getCards(
                query = query,
                page = page,
                pageSize = state.config.pageSize,
                select = select,
                orderBy = orderBy
            )

            val endOfPaginationReached = response.data.isEmpty()

            transactionRunner {
                if (loadType == LoadType.REFRESH) {
                    cardRemoteKeyDao.clearRemoteKeysBySet(setId)
                    cardDao.clearCardsBySet(setId)
                }
                val prevKey = if (page == 1) null else page - 1
                val nextKey = if (endOfPaginationReached) null else page + 1
                val keys = response.data.map { card ->
                    CardRemoteKeysEntity(
                        cardId = card.id,
                        setId = setId,
                        prevKey = prevKey,
                        nextKey = nextKey,
                    )
                }
                cardRemoteKeyDao.insertAll(keys)
                cardDao.insertMany(response.data.map(CardResponse::asEntity))
            }

            return MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
        } catch (exception: IOException) {
            return MediatorResult.Error(exception)
        } catch (exception: Exception) {
            return MediatorResult.Error(exception)
        }
    }

    private suspend fun getRemoteKeyForLastItem(
        state: PagingState<Int, CardEntity>
    ): CardRemoteKeysEntity? {
        return state.pages.lastOrNull { it.data.isNotEmpty() }?.data?.lastOrNull()
            ?.let { card -> cardRemoteKeyDao.remoteKeysCardId(card.id) }
    }

    private suspend fun getRemoteKeyForFirstItem(
        state: PagingState<Int, CardEntity>
    ): CardRemoteKeysEntity? {
        return state.pages.firstOrNull { it.data.isNotEmpty() }?.data?.firstOrNull()
            ?.let { card -> cardRemoteKeyDao.remoteKeysCardId(card.id) }
    }

    private suspend fun getRemoteKeyClosestToCurrentPosition(
        state: PagingState<Int, CardEntity>
    ): CardRemoteKeysEntity? {
        return state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.id?.let { cardId ->
                cardRemoteKeyDao.remoteKeysCardId(cardId)
            }
        }
    }
}
