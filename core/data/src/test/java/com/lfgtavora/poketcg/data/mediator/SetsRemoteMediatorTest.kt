package com.lfgtavora.poketcg.data.mediator

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingConfig
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import com.google.common.truth.Truth.assertThat
import com.lfgtavora.poketcg.core.crashlytics.TestCrashlyticsHelper
import com.lfgtavora.poketcg.database.dao.SetDao
import com.lfgtavora.poketcg.database.dao.SetRemoteKeyDao
import com.lfgtavora.poketcg.database.model.SetEntity
import com.lfgtavora.poketcg.database.model.SetRemoteKeysEntity
import com.lfgtavora.poketcg.network.model.SetDataListResponse
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
    private val transactionRunner = TransactionRunner { block -> block() }
    private val crashlytics = TestCrashlyticsHelper()
    private val fetchCalls = mutableListOf<Pair<Int, Int>>()
    private var fetchResult: Result<SetDataListResponse> =
        Result.failure(IllegalStateException("not stubbed"))
    private lateinit var mediator: SetsRemoteMediator

    @Before
    fun setUp() {
        fetchCalls.clear()
        mediator = SetsRemoteMediator(
            setDao = setDao,
            setRemoteKeyDao = setRemoteKeyDao,
            fetchNetWorkData = { page, pageSize ->
                fetchCalls += page to pageSize
                fetchResult
            },
            transactionRunner = transactionRunner,
            crashlytics = crashlytics,
        )
    }

    @Test
    fun `first initialize always launches refresh`() = runTest {
        assertThat(mediator.initialize())
            .isEqualTo(RemoteMediator.InitializeAction.LAUNCH_INITIAL_REFRESH)
    }

    @Test
    fun `second initialize skips to avoid invalidation loop`() = runTest {
        mediator.initialize()

        assertThat(mediator.initialize())
            .isEqualTo(RemoteMediator.InitializeAction.SKIP_INITIAL_REFRESH)
    }

    @Test
    fun `refresh always fetches page 1 even with scrolled state`() = runTest {
        fetchResult = Result.success(listResponse(sampleSet("sv1")))
        val set = sampleEntity("sv1")
        coEvery { setRemoteKeyDao.remoteKeysSetId("sv1") } returns SetRemoteKeysEntity(
            setId = "sv1",
            prevKey = 1,
            nextKey = 3,
        )

        mediator.load(LoadType.REFRESH, pagingStateWith(set))

        assertThat(fetchCalls).containsExactly(1 to 20)
    }

    @Test
    fun `refresh with empty state fetches page 1 and upserts without clearing`() = runTest {
        fetchResult = Result.success(listResponse(sampleSet("sv1"), sampleSet("sv2")))

        val keysSlot = slot<List<SetRemoteKeysEntity>>()
        coEvery { setRemoteKeyDao.insertAll(capture(keysSlot)) } returns Unit

        val result = mediator.load(LoadType.REFRESH, emptyPagingState(pageSize = 2))

        assertThat((result as RemoteMediator.MediatorResult.Success).endOfPaginationReached).isFalse()
        coVerify(exactly = 0) { setRemoteKeyDao.clearRemoteKeys() }
        coVerify(exactly = 0) { setDao.clearAll() }
        coVerify { setDao.insertMany(match { it.size == 2 }) }
        assertThat(keysSlot.captured).hasSize(2)
        assertThat(keysSlot.captured[0].prevKey).isNull()
        assertThat(keysSlot.captured[0].nextKey).isEqualTo(2)
        assertThat(fetchCalls).containsExactly(1 to 2)
    }

    @Test
    fun `append with nextKey uses that page`() = runTest {
        val set = sampleEntity("sv1")
        coEvery { setRemoteKeyDao.remoteKeysSetId("sv1") } returns SetRemoteKeysEntity(
            setId = "sv1",
            prevKey = 1,
            nextKey = 3,
        )
        fetchResult = Result.success(listResponse(sampleSet("sv3"), page = 3))

        val result = mediator.load(LoadType.APPEND, pagingStateWith(set))

        assertThat(result).isInstanceOf(RemoteMediator.MediatorResult.Success::class.java)
        assertThat(fetchCalls).containsExactly(3 to 20)
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
        assertThat(fetchCalls).isEmpty()
    }

    @Test
    fun `prepend always ends without network even if prevKey exists`() = runTest {
        val set = sampleEntity("sv1")
        coEvery { setRemoteKeyDao.remoteKeysSetId("sv1") } returns SetRemoteKeysEntity(
            setId = "sv1",
            prevKey = 1,
            nextKey = 3,
        )

        val result = mediator.load(LoadType.PREPEND, pagingStateWith(set))

        assertThat((result as RemoteMediator.MediatorResult.Success).endOfPaginationReached).isTrue()
        assertThat(fetchCalls).isEmpty()
    }

    @Test
    fun `empty network list reaches end and writes null nextKey`() = runTest {
        fetchResult = Result.success(
            SetDataListResponse(
                data = emptyList(),
                page = 1,
                pageSize = 20,
                count = 0,
                totalCount = 0,
            )
        )

        val keysSlot = slot<List<SetRemoteKeysEntity>>()
        coEvery { setRemoteKeyDao.insertAll(capture(keysSlot)) } returns Unit

        val result = mediator.load(LoadType.REFRESH, emptyPagingState())

        assertThat((result as RemoteMediator.MediatorResult.Success).endOfPaginationReached).isTrue()
        assertThat(keysSlot.captured).isEmpty()
    }

    @Test
    fun `ioException returns error and records crashlytics`() = runTest {
        val error = IOException("offline")
        fetchResult = Result.failure(error)

        val result = mediator.load(LoadType.REFRESH, emptyPagingState())

        assertThat(result).isInstanceOf(RemoteMediator.MediatorResult.Error::class.java)
        assertThat(crashlytics.exceptions).containsExactly(error)
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

    private fun listResponse(
        vararg sets: SetResponse,
        page: Int = 1,
        totalCount: Int = 100,
    ) = SetDataListResponse(
        data = sets.toList(),
        page = page,
        pageSize = 20,
        count = sets.size,
        totalCount = totalCount,
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
