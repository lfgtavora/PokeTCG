package com.lfgtavora.poketcg.data.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.lfgtavora.poketcg.data.mapper.asEntity
import com.lfgtavora.poketcg.data.mediator.CardsRemoteMediator
import com.lfgtavora.poketcg.database.dao.CardDao
import com.lfgtavora.poketcg.database.dao.SetDao
import com.lfgtavora.poketcg.database.model.CardEntity
import com.lfgtavora.poketcg.database.model.asCardPreview
import com.lfgtavora.poketcg.model.CardPreview
import com.lfgtavora.poketcg.network.TcgDexNetworkDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject

class OfflineFirstCardRepository @Inject constructor(
    val remoteDataSource: TcgDexNetworkDataSource,
    val cardDao: CardDao,
    val setDao: SetDao,
) : CardRepository {

    @OptIn(ExperimentalPagingApi::class)
    override fun getCardsFromSet(
        setId: String,
        pageSize: Int,
        query: String,
        select: String
    ): Flow<PagingData<CardPreview>> {
        return Pager(
            config = PagingConfig(pageSize),
            remoteMediator = CardsRemoteMediator(
                setId = setId,
                query = query,
                select = select,
                cardDao = cardDao,
                network = remoteDataSource
            ),
            pagingSourceFactory = { cardDao.getCardsBySet(setId) }
        ).flow.map { pagingData ->
            pagingData.map(CardEntity::asCardPreview)
        }
    }

    override fun getCard(id: String): Flow<CardEntity?> =
        cardDao.getCardById(id).onStart {
            refreshCard(id)
        }.flowOn(Dispatchers.IO)

    private suspend fun refreshCard(id: String) =
        runCatching {
            val card = remoteDataSource.getCard(id).data
            card.set?.let { setDao.insertIfAbsent(it.asEntity()) }
            cardDao.upsert(card.asEntity())
        }
}