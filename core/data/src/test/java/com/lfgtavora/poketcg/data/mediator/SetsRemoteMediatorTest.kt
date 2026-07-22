package com.lfgtavora.poketcg.data.mediator

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingConfig
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import com.google.common.truth.Truth.assertThat
import com.lfgtavora.poketcg.database.dao.SetDao
import com.lfgtavora.poketcg.database.dao.SetRemoteKeyDao
import com.lfgtavora.poketcg.database.model.SetEntity
import com.lfgtavora.poketcg.database.model.SetRemoteKeysEntity
import com.lfgtavora.poketcg.network.TcgDexNetworkDataSource
import com.lfgtavora.poketcg.network.model.SetResponse
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import java.io.IOException

@OptIn(ExperimentalPagingApi::class)
class SetsRemoteMediatorTest {

    private val setDao = mockk<SetDao>(relaxed = true)
    private val setRemoteKeyDao = mockk<SetRemoteKeyDao>(relaxed = true)
    private val network = mockk<TcgDexNetworkDataSource>()
    private val transactionRunner = TransactionRunner { block -> block() }
    private lateinit var mediator: SetsRemoteMediator

    @Before
    fun setUp() {
        mediator = SetsRemoteMediator(
            setDao = setDao,
            setRemoteKeyDao = setRemoteKeyDao,
            network = network,
            transactionRunner = transactionRunner,
        )
    }

    @Test
    fun `refresh with empty state fetches page 1 clears and inserts`() = runTest {
        coEvery {
            network.getSetsBrief(page = 1, pageSize = 20, orderBy = "-releaseDate", field = "releaseDate")
        } returns listOf(sampleSet("sv1"), sampleSet("sv2"))

        val keysSlot = slot<List<SetRemoteKeysEntity>>()
        coEvery { setRemoteKeyDao.insertAll(capture(keysSlot)) } returns Unit

        val result = mediator.load(LoadType.REFRESH, emptyPagingState())

        assertThat((result as RemoteMediator.MediatorResult.Success).endOfPaginationReached).isFalse()
        coVerify { setRemoteKeyDao.clearRemoteKeys() }
        coVerify { setDao.clearAll() }
        coVerify { setDao.insertMany(match { it.size == 2 }) }
        assertThat(keysSlot.captured).hasSize(2)
        assertThat(keysSlot.captured[0].prevKey).isNull()
        assertThat(keysSlot.captured[0].nextKey).isEqualTo(2)
    }

    @Test
    fun `append with nextKey uses that page`() = runTest {
        val set = sampleEntity("sv1")
        coEvery { setRemoteKeyDao.remoteKeysSetId("sv1") } returns SetRemoteKeysEntity(
            setId = "sv1",
            prevKey = 1,
            nextKey = 3,
        )
        coEvery {
            network.getSetsBrief(page = 3, pageSize = 20, orderBy = "-releaseDate", field = "releaseDate")
        } returns listOf(sampleSet("sv3"))

        val result = mediator.load(LoadType.APPEND, pagingStateWith(set))

        assertThat(result).isInstanceOf(RemoteMediator.MediatorResult.Success::class.java)
        coVerify {
            network.getSetsBrief(page = 3, pageSize = 20, orderBy = "-releaseDate", field = "releaseDate")
        }
    }

    @Test
    fun `append with null nextKey ends without network`() = runTest {
        val set = sampleEntity("sv1")
        coEvery { setRemoteKeyDao.remoteKeysSetId("sv1") } returns SetRemoteKeysEntity(
            setId = "sv1",
            prevKey = 1,
            nextKey = null,
        )

        val result = mediator.load(LoadType.APPEND, pagingStateWith(set))

        assertThat((result as RemoteMediator.MediatorResult.Success).endOfPaginationReached).isTrue()
        coVerify(exactly = 0) { network.getSetsBrief(any(), any(), any(), any()) }
    }

    @Test
    fun `prepend with null prevKey ends when keys exist`() = runTest {
        val set = sampleEntity("sv1")
        coEvery { setRemoteKeyDao.remoteKeysSetId("sv1") } returns SetRemoteKeysEntity(
            setId = "sv1",
            prevKey = null,
            nextKey = 2,
        )

        val result = mediator.load(LoadType.PREPEND, pagingStateWith(set))

        assertThat((result as RemoteMediator.MediatorResult.Success).endOfPaginationReached).isTrue()
        coVerify(exactly = 0) { network.getSetsBrief(any(), any(), any(), any()) }
    }

    @Test
    fun `empty network list reaches end and writes null nextKey`() = runTest {
        coEvery {
            network.getSetsBrief(any(), any(), any(), any())
        } returns emptyList()

        val keysSlot = slot<List<SetRemoteKeysEntity>>()
        coEvery { setRemoteKeyDao.insertAll(capture(keysSlot)) } returns Unit

        val result = mediator.load(LoadType.REFRESH, emptyPagingState())

        assertThat((result as RemoteMediator.MediatorResult.Success).endOfPaginationReached).isTrue()
        assertThat(keysSlot.captured).isEmpty()
    }

    @Test
    fun `ioException returns error`() = runTest {
        coEvery {
            network.getSetsBrief(any(), any(), any(), any())
        } throws IOException("offline")

        val result = mediator.load(LoadType.REFRESH, emptyPagingState())

        assertThat(result).isInstanceOf(RemoteMediator.MediatorResult.Error::class.java)
    }

    private fun emptyPagingState(pageSize: Int = 20) = PagingState<Int, SetEntity>(
        pages = emptyList(),
        anchorPosition = null,
        config = PagingConfig(pageSize = pageSize),
        leadingPlaceholderCount = 0,
    )

    private fun pagingStateWith(set: SetEntity, pageSize: Int = 20) = PagingState(
        pages = listOf(
            PagingSource.LoadResult.Page(
                data = listOf(set),
                prevKey = null,
                nextKey = 2,
            )
        ),
        anchorPosition = 0,
        config = PagingConfig(pageSize = pageSize),
        leadingPlaceholderCount = 0,
    )

    private fun sampleSet(id: String) = SetResponse(
        id = id,
        name = "Set $id",
        printedTotal = 100,
        total = 100,
        releaseDate = "2023/01/01",
    )

    private fun sampleEntity(id: String) = SetEntity(
        id = id,
        name = "Set $id",
        printedTotal = 100,
        total = 100,
        releaseDate = "2023/01/01",
    )
}
