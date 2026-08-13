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
import com.lfgtavora.poketcg.data.mediator.CardsRemoteMediator
import com.lfgtavora.poketcg.database.PokeTcgDatabase
import com.lfgtavora.poketcg.database.dao.CardDao
import com.lfgtavora.poketcg.database.dao.SetDao
import com.lfgtavora.poketcg.database.model.CardEntity
import com.lfgtavora.poketcg.database.model.asCard
import com.lfgtavora.poketcg.database.model.asCardPreview
import com.lfgtavora.poketcg.model.data.Card
import com.lfgtavora.poketcg.model.data.CardLookup
import com.lfgtavora.poketcg.model.data.CardPreview
import com.lfgtavora.poketcg.network.TcgDexNetworkDataSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.io.IOException
import javax.inject.Inject

class OfflineFirstCardRepository @Inject constructor(
    val remoteDataSource: TcgDexNetworkDataSource,
    val cardDao: CardDao,
    val setDao: SetDao,
    private val database: PokeTcgDatabase,
    private val crashlytics: CrashlyticsHelper,
    @param:IoDispatcher private val ioDispatcher: CoroutineDispatcher,
) : CardRepository {

    @OptIn(ExperimentalPagingApi::class)
    override fun getCardsFromSet(
        setId: String,
        pageSize: Int,
        query: String,
        select: String,
        orderBy: String?
    ): Flow<PagingData<CardPreview>> {
        return Pager(
            config = PagingConfig(
                pageSize = pageSize,
                prefetchDistance = pageSize,
                enablePlaceholders = false,
            ),
            remoteMediator = CardsRemoteMediator(
                setId = setId,
                query = query,
                select = select,
                orderBy = orderBy,
                cardDao = cardDao,
                cardRemoteKeyDao = database.cardRemoteKeyDao(),
                network = remoteDataSource,
                transactionRunner = { block ->
                    database.withTransaction { block() }
                },
            ),
            pagingSourceFactory = { cardDao.getCardsBySet(setId) }
        ).flow.map { pagingData ->
            pagingData.map(CardEntity::asCardPreview)
        }
    }

    override fun observeCard(id: String): Flow<CardLookup?> =
        cardDao.getCardById(id)
            .map { entity ->
                when {
                    entity == null -> null
                    entity.lastFullSyncAt != null -> CardLookup.Full(entity.asCard())
                    else -> CardLookup.Partial(entity.asCardPreview())
                }
            }
            .flowOn(ioDispatcher)

    override suspend fun syncCard(
        id: String,
        retryPolicy: RetryPolicy
    ) =
        withContext(ioDispatcher) {
            suspendRunCatching {
                val card = withRetry(retryPolicy) { remoteDataSource.getCard(id).data }
                card.set?.let { setDao.insertIfAbsent(it.asEntity()) }
                cardDao.upsert(card.asEntity())
            }.onFailure { throwable ->
                if (throwable !is IOException) {
                    crashlytics.recordException(
                        throwable = throwable,
                        extras = mapOf(
                            "card_id" to id,
                            "retry_police" to retryPolicy.toString()
                        ),
                    )
                }
            }
        }
}
