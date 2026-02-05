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
    private val select: String
) : RemoteMediator<Int, CardEntity>() {

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

                    if (count == state.pages.size) {
                        return MediatorResult.Success(endOfPaginationReached = true)
                    }

                    (count / state.config.pageSize) + 1

                }
            }

            val response = network.getCards(
                query = query,
                page = page,
                pageSize = state.config.pageSize,
                select = select
            )

            val currentItemsCount = cardDao.getCardsCountBySet(setId)
            val endOfPaginationReached =
                (currentItemsCount + response.data.size) >= response.totalCount

            cardDao.insertMany(response.data.map(CardResponse::asEntity))

            return MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)

        } catch (exception: IOException) {
            return MediatorResult.Error(exception)
        } catch (exception: Exception) {
            return MediatorResult.Error(exception)
        }
    }
}