package com.lfgtavora.poketcg.data.mediator

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingConfig
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import com.google.common.truth.Truth.assertThat
import com.lfgtavora.poketcg.database.dao.CardDao
import com.lfgtavora.poketcg.database.model.CardEntity
import com.lfgtavora.poketcg.network.TcgDexNetworkDataSource
import com.lfgtavora.poketcg.network.model.CardDataListResponse
import com.lfgtavora.poketcg.network.model.CardResponse
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import java.io.IOException

@OptIn(ExperimentalPagingApi::class)
class CardsRemoteMediatorTest {

    private val cardDao = mockk<CardDao>(relaxed = true)
    private val network = mockk<TcgDexNetworkDataSource>()
    private lateinit var mediator: CardsRemoteMediator

    @Before
    fun setUp() {
        mediator = CardsRemoteMediator(
            setId = "sv1",
            cardDao = cardDao,
            network = network,
            query = "set.id:sv1",
            select = "id,name,number,images",
            orderBy = "number",
        )
    }

    @Test
    fun `initialize skips when cache has cards`() = runTest {
        coEvery { cardDao.getCardsCountBySet("sv1") } returns 10

        assertThat(mediator.initialize())
            .isEqualTo(RemoteMediator.InitializeAction.SKIP_INITIAL_REFRESH)
    }

    @Test
    fun `initialize launches when cache empty`() = runTest {
        coEvery { cardDao.getCardsCountBySet("sv1") } returns 0

        assertThat(mediator.initialize())
            .isEqualTo(RemoteMediator.InitializeAction.LAUNCH_INITIAL_REFRESH)
    }

    @Test
    fun `prepend returns end without network`() = runTest {
        val result = mediator.load(LoadType.PREPEND, emptyPagingState())

        assertThat(result).isInstanceOf(RemoteMediator.MediatorResult.Success::class.java)
        assertThat((result as RemoteMediator.MediatorResult.Success).endOfPaginationReached).isTrue()
        coVerify(exactly = 0) { network.getCards(any(), any(), any(), any(), any()) }
    }

    @Test
    fun `append happy path inserts and uses computed page`() = runTest {
        coEvery { cardDao.getCardsCountBySet("sv1") } returnsMany listOf(32, 64)
        coEvery {
            network.getCards(
                query = "set.id:sv1",
                page = 2,
                pageSize = 32,
                select = "id,name,number,images",
                orderBy = "number",
            )
        } returns CardDataListResponse(
            data = listOf(sampleCard("sv1-33")),
            page = 2,
            pageSize = 32,
            count = 1,
            totalCount = 100,
        )

        val result = mediator.load(LoadType.APPEND, emptyPagingState(pageSize = 32))

        assertThat(result).isInstanceOf(RemoteMediator.MediatorResult.Success::class.java)
        assertThat((result as RemoteMediator.MediatorResult.Success).endOfPaginationReached).isFalse()
        coVerify { cardDao.insertMany(match { it.size == 1 && it[0].id == "sv1-33" }) }
    }

    @Test
    fun `empty response reaches end of pagination`() = runTest {
        coEvery { cardDao.getCardsCountBySet("sv1") } returns 0
        coEvery {
            network.getCards(any(), any(), any(), any(), any())
        } returns CardDataListResponse(
            data = emptyList(),
            page = 1,
            pageSize = 32,
            count = 0,
            totalCount = 0,
        )

        val result = mediator.load(LoadType.REFRESH, emptyPagingState())

        assertThat((result as RemoteMediator.MediatorResult.Success).endOfPaginationReached).isTrue()
    }

    @Test
    fun `count reaching totalCount reaches end`() = runTest {
        coEvery { cardDao.getCardsCountBySet("sv1") } returns 100
        coEvery {
            network.getCards(any(), page = 1, pageSize = any(), select = any(), orderBy = any())
        } returns CardDataListResponse(
            data = listOf(sampleCard("sv1-100")),
            page = 1,
            pageSize = 32,
            count = 1,
            totalCount = 100,
        )

        val result = mediator.load(LoadType.REFRESH, emptyPagingState())

        assertThat((result as RemoteMediator.MediatorResult.Success).endOfPaginationReached).isTrue()
    }

    @Test
    fun `ioException returns error`() = runTest {
        coEvery { cardDao.getCardsCountBySet("sv1") } returns 0
        coEvery {
            network.getCards(any(), any(), any(), any(), any())
        } throws IOException("offline")

        val result = mediator.load(LoadType.REFRESH, emptyPagingState())

        assertThat(result).isInstanceOf(RemoteMediator.MediatorResult.Error::class.java)
        assertThat((result as RemoteMediator.MediatorResult.Error).throwable)
            .isInstanceOf(IOException::class.java)
    }

    private fun emptyPagingState(pageSize: Int = 32) = PagingState<Int, CardEntity>(
        pages = emptyList(),
        anchorPosition = null,
        config = PagingConfig(pageSize = pageSize),
        leadingPlaceholderCount = 0,
    )

    private fun sampleCard(id: String) = CardResponse(
        id = id,
        name = "Card",
        number = "1",
        set = com.lfgtavora.poketcg.network.model.SetResponse(id = "sv1"),
    )
}
