package com.lfgtavora.poketcg.data.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.lfgtavora.poketcg.data.di.IoDispatcher
import androidx.room.withTransaction
import com.lfgtavora.poketcg.data.mediator.SetsRemoteMediator
import com.lfgtavora.poketcg.data.mediator.TransactionRunner
import com.lfgtavora.poketcg.database.PokeTcgDatabase
import com.lfgtavora.poketcg.database.dao.CardDao
import com.lfgtavora.poketcg.database.dao.SetDao
import com.lfgtavora.poketcg.database.model.SetEntity
import com.lfgtavora.poketcg.database.model.asPreviewModel
import com.lfgtavora.poketcg.model.SetPreview
import com.lfgtavora.poketcg.network.TcgDexNetworkDataSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class OfflineFirstSetRepository @Inject constructor(
    private val network: TcgDexNetworkDataSource,
    private val setDao: SetDao,
    private val cardDao: CardDao,
    private val database: PokeTcgDatabase,
    @param:IoDispatcher private val ioDispatcher: CoroutineDispatcher,
) : SetRepository {

    @OptIn(ExperimentalPagingApi::class)
    override fun getSets(): Flow<PagingData<SetPreview>> = Pager(
        config = PagingConfig(
            pageSize = SETS_PER_PAGE,
            enablePlaceholders = true
        ),
        remoteMediator = SetsRemoteMediator(
            setDao = setDao,
            setRemoteKeyDao = database.setRemoteKeyDao(),
            network = network,
            transactionRunner = { block ->
                database.withTransaction { block() }
            },
        ),
        pagingSourceFactory = { setDao.pagingSource() }
    ).flow.map { pagingData ->
        pagingData.map(SetEntity::asPreviewModel)
    }

    override fun getSet(id: String): Flow<SetPreview> {
        return setDao
            .getById(id)
            .map { it.asPreviewModel() }
            .flowOn(ioDispatcher)
    }

    companion object {
        private const val SETS_PER_PAGE = 20
    }
}
