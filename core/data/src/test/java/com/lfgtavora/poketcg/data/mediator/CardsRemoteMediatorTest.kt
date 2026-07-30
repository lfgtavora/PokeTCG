package com.lfgtavora.poketcg.data.mediator

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingConfig
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import com.google.common.truth.Truth.assertThat
import com.lfgtavora.poketcg.database.dao.CardDao
import com.lfgtavora.poketcg.database.dao.CardRemoteKeyDao
import com.lfgtavora.poketcg.database.model.CardEntity
import com.lfgtavora.poketcg.database.model.CardRemoteKeysEntity
import com.lfgtavora.poketcg.network.TcgDexNetworkDataSource
import com.lfgtavora.poketcg.network.model.CardDataListResponse
import com.lfgtavora.poketcg.network.model.CardResponse
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import java.io.IOException

@OptIn(ExperimentalPagingApi::class)
class CardsRemoteMediatorTest {

    private val cardDao = mockk<CardDao>(relaxed = true)
    private val cardRemoteKeyDao = mockk<CardRemoteKeyDao>(relaxed = true)
    private val network = mockk<TcgDexNetworkDataSource>()
    private val transactionRunner = TransactionRunner { block -> block() }
    private lateinit var mediator: CardsRemoteMediator

    @Before
    fun setUp() {
        mediator = CardsRemoteMediator(
            setId = "sv1",
            cardDao = cardDao,
            cardRemoteKeyDao = cardRemoteKeyDao,
            network = network,
            query = "set.id:sv1",
            select = "id,name,number,images",
            orderBy = "number",
            transactionRunner = transactionRunner,
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
    fun `refresh with empty state fetches page 1 clears and inserts`() = runTest {
        coEvery {
            network.getCards(
                query = "set.id:sv1",
                page = 1,
                pageSize = 48,
                select = "id,name,number,images",
                orderBy = "number",
            )
        } returns CardDataListResponse(
            data = listOf(sampleCard("sv1-1"), sampleCard("sv1-2")),
            page = 1,
            pageSize = 48,
            count = 2,
            totalCount = 100,
        )

        val keysSlot = slot<List<CardRemoteKeysEntity>>()
        coEvery { cardRemoteKeyDao.insertAll(capture(keysSlot)) } returns Unit

        val result = mediator.load(LoadType.REFRESH, emptyPagingState())

        assertThat((result as RemoteMediator.MediatorResult.Success).endOfPaginationReached).isFalse()
        coVerify { cardRemoteKeyDao.clearRemoteKeysBySet("sv1") }
        coVerify { cardDao.clearCardsBySet("sv1") }
        coVerify { cardDao.insertMany(match { it.size == 2 }) }
        assertThat(keysSlot.captured).hasSize(2)
        assertThat(keysSlot.captured[0].prevKey).isNull()
        assertThat(keysSlot.captured[0].nextKey).isEqualTo(2)
        assertThat(keysSlot.captured[0].setId).isEqualTo("sv1")
    }

    @Test
    fun `append with nextKey uses that page`() = runTest {
        val card = sampleEntity("sv1-1")
        coEvery { cardRemoteKeyDao.remoteKeysCardId("sv1-1") } returns CardRemoteKeysEntity(
            cardId = "sv1-1",
            setId = "sv1",
            prevKey = 1,
            nextKey = 3,
        )
        coEvery {
            network.getCards(
                query = "set.id:sv1",
                page = 3,
                pageSize = 48,
                select = "id,name,number,images",
                orderBy = "number",
            )
        } returns CardDataListResponse(
            data = listOf(sampleCard("sv1-33")),
            page = 3,
            pageSize = 48,
            count = 1,
            totalCount = 100,
        )

        val result = mediator.load(LoadType.APPEND, pagingStateWith(card))

        assertThat(result).isInstanceOf(RemoteMediator.MediatorResult.Success::class.java)
        coVerify {
            network.getCards(
                query = "set.id:sv1",
                page = 3,
                pageSize = 48,
                select = "id,name,number,images",
                orderBy = "number",
            )
        }
    }

    @Test
    fun `append with null nextKey ends without network`() = runTest {
        val card = sampleEntity("sv1-1")
        coEvery { cardRemoteKeyDao.remoteKeysCardId("sv1-1") } returns CardRemoteKeysEntity(
            cardId = "sv1-1",
            setId = "sv1",
            prevKey = 1,
            nextKey = null,
        )

        val result = mediator.load(LoadType.APPEND, pagingStateWith(card))

        assertThat((result as RemoteMediator.MediatorResult.Success).endOfPaginationReached).isTrue()
        coVerify(exactly = 0) { network.getCards(any(), any(), any(), any(), any()) }
    }

    @Test
    fun `prepend with null prevKey ends when keys exist`() = runTest {
        val card = sampleEntity("sv1-1")
        coEvery { cardRemoteKeyDao.remoteKeysCardId("sv1-1") } returns CardRemoteKeysEntity(
            cardId = "sv1-1",
            setId = "sv1",
            prevKey = null,
            nextKey = 2,
        )

        val result = mediator.load(LoadType.PREPEND, pagingStateWith(card))

        assertThat((result as RemoteMediator.MediatorResult.Success).endOfPaginationReached).isTrue()
        coVerify(exactly = 0) { network.getCards(any(), any(), any(), any(), any()) }
    }

    @Test
    fun `empty network list reaches end and writes null nextKey`() = runTest {
        coEvery {
            network.getCards(any(), any(), any(), any(), any())
        } returns CardDataListResponse(
            data = emptyList(),
            page = 1,
            pageSize = 48,
            count = 0,
            totalCount = 0,
        )

        val keysSlot = slot<List<CardRemoteKeysEntity>>()
        coEvery { cardRemoteKeyDao.insertAll(capture(keysSlot)) } returns Unit

        val result = mediator.load(LoadType.REFRESH, emptyPagingState())

        assertThat((result as RemoteMediator.MediatorResult.Success).endOfPaginationReached).isTrue()
        assertThat(keysSlot.captured).isEmpty()
    }

    @Test
    fun `ioException returns error`() = runTest {
        coEvery {
            network.getCards(any(), any(), any(), any(), any())
        } throws IOException("offline")

        val result = mediator.load(LoadType.REFRESH, emptyPagingState())

        assertThat(result).isInstanceOf(RemoteMediator.MediatorResult.Error::class.java)
        assertThat((result as RemoteMediator.MediatorResult.Error).throwable)
            .isInstanceOf(IOException::class.java)
    }

    private fun emptyPagingState(pageSize: Int = 48) = PagingState<Int, CardEntity>(
        pages = emptyList(),
        anchorPosition = null,
        config = PagingConfig(pageSize = pageSize),
        leadingPlaceholderCount = 0,
    )

    private fun pagingStateWith(card: CardEntity, pageSize: Int = 48) = PagingState(
        pages = listOf(
            PagingSource.LoadResult.Page(
                data = listOf(card),
                prevKey = null,
                nextKey = 2,
            )
        ),
        anchorPosition = 0,
        config = PagingConfig(pageSize = pageSize),
        leadingPlaceholderCount = 0,
    )

    private fun sampleCard(id: String) = CardResponse(
        id = id,
        name = "Card",
        number = "1",
        set = com.lfgtavora.poketcg.network.model.SetResponse(id = "sv1"),
    )

    private fun sampleEntity(id: String) = CardEntity(
        id = id,
        name = "Card",
        supertype = "Pokémon",
        number = "1",
        sortNumber = 1,
        setId = "sv1",
    )
}
