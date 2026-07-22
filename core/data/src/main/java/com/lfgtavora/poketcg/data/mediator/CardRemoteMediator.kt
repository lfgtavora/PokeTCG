package com.lfgtavora.poketcg.data.mediator

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import com.lfgtavora.poketcg.data.mapper.asEntity
import com.lfgtavora.poketcg.database.dao.CardDao
import com.lfgtavora.poketcg.database.model.CardEntity
import com.lfgtavora.poketcg.network.TcgDexNetworkDataSource
import com.lfgtavora.poketcg.network.model.CardResponse
import java.io.IOException

@OptIn(ExperimentalPagingApi::class)
internal class CardsRemoteMediator(
    private val setId: String,
    private val cardDao: CardDao,
    private val network: TcgDexNetworkDataSource,
    private val query: String,
    private val select: String,
    private val orderBy: String? = null
) : RemoteMediator<Int, CardEntity>() {

    override suspend fun initialize(): InitializeAction {
        return cardsInitializeAction(cardDao.getCardsCountBySet(setId))
    }

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, CardEntity>
    ): MediatorResult {
        try {
            val page = when (loadType) {
                LoadType.PREPEND -> null
                LoadType.REFRESH -> cardsPage(LoadType.REFRESH, cachedCount = 0, pageSize = state.config.pageSize)
                LoadType.APPEND -> cardsPage(
                    loadType = LoadType.APPEND,
                    cachedCount = cardDao.getCardsCountBySet(setId),
                    pageSize = state.config.pageSize,
                )
            } ?: return MediatorResult.Success(endOfPaginationReached = true)

            val response = network.getCards(
                query = query,
                page = page,
                pageSize = state.config.pageSize,
                select = select,
                orderBy = orderBy
            )

            cardDao.insertMany(response.data.map(CardResponse::asEntity))

            val currentItemsCount = cardDao.getCardsCountBySet(setId)
            val endOfPaginationReached = cardsEndOfPagination(
                empty = response.data.isEmpty(),
                currentCount = currentItemsCount,
                totalCount = response.totalCount,
            )

            return MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)

        } catch (exception: IOException) {
            return MediatorResult.Error(exception)
        } catch (exception: Exception) {
            return MediatorResult.Error(exception)
        }
    }
}
