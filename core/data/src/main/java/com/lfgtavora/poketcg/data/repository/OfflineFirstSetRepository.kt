package com.lfgtavora.poketcg.data.repository

import com.lfgtavora.poketcg.database.dao.SetDao
import com.lfgtavora.poketcg.database.model.SetEntity
import com.lfgtavora.poketcg.database.model.asModel
import com.lfgtavora.poketcg.model.SetData
import com.lfgtavora.poketcg.network.TcgDexNetworkDataSource
import com.lfgtavora.poketcg.network.model.SetBriefResponse
import com.lfgtavora.poketcg.network.model.SetResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject

class OfflineFirstSetRepository @Inject constructor(
    private val network: TcgDexNetworkDataSource,
    private val setDao: SetDao
) : SetRepository {

    override fun getAllSetsPaginated(
        page: Int,
        itemsPerPage: Int,
        orderBy: String?,
        field: String?
    ): Flow<List<SetData>> = setDao.getAll(
        page = page,
        itemsPerPage = itemsPerPage,
        orderBy = orderBy,
        field = field
    ).map { result ->
        result.map(SetEntity::asModel)
    }.onStart {
        syncSetFromNetwork(
            page = page,
            itemsPerPage = itemsPerPage,
            orderBy = orderBy,
            field = field
        )
    }

    override fun getSet(id: String): Flow<SetResponse?> {
        TODO("Not yet implemented")
    }

    private suspend fun syncSetFromNetwork(
        page: Int,
        itemsPerPage: Int,
        orderBy: String?,
        field: String?
    ) {
        try {
            val networkResponse = network.getSets(
                page = page,
                itemsPerPage = itemsPerPage,
                orderBy = orderBy,
                field = field
            )?.map(SetBriefResponse::asEntity)

            if (networkResponse != null) {
                setDao.insert()
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}
