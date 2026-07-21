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
        val cachedCount = cardDao.getCardsCountBySet(setId)
        return if (cachedCount > 0) {
            InitializeAction.SKIP_INITIAL_REFRESH
        } else {
            InitializeAction.LAUNCH_INITIAL_REFRESH
        }
    }

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, CardEntity>
    ): MediatorResult {
        try {
            val page = when (loadType) {
                LoadType.REFRESH -> 1
                LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
                LoadType.APPEND -> {
                    val count = cardDao.getCardsCountBySet(setId)
                    if (count == 0) 1 else (count / state.config.pageSize) + 1
                }
            }

            val response = network.getCards(
                query = query,
                page = page,
                pageSize = state.config.pageSize,
                select = select,
                orderBy = orderBy
            )

            cardDao.insertMany(response.data.map(CardResponse::asEntity))

            val currentItemsCount = cardDao.getCardsCountBySet(setId)
            val endOfPaginationReached = response.data.isEmpty() ||
                currentItemsCount >= response.totalCount

            return MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)

        } catch (exception: IOException) {
            return MediatorResult.Error(exception)
        } catch (exception: Exception) {
            return MediatorResult.Error(exception)
        }
    }
}