package com.lfgtavora.poketcg.data.mediator

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.RemoteMediator
import com.google.common.truth.Truth.assertThat
import org.junit.Test

@OptIn(ExperimentalPagingApi::class)
class CardsPaginationTest {

    @Test
    fun `initialize launches refresh when cache empty`() {
        assertThat(cardsInitializeAction(0))
            .isEqualTo(RemoteMediator.InitializeAction.LAUNCH_INITIAL_REFRESH)
    }

    @Test
    fun `initialize skips refresh when cache has items`() {
        assertThat(cardsInitializeAction(1))
            .isEqualTo(RemoteMediator.InitializeAction.SKIP_INITIAL_REFRESH)
    }

    @Test
    fun `page for prepend is null`() {
        assertThat(cardsPage(LoadType.PREPEND, cachedCount = 32, pageSize = 32)).isNull()
    }

    @Test
    fun `page for refresh is always 1`() {
        assertThat(cardsPage(LoadType.REFRESH, cachedCount = 99, pageSize = 32)).isEqualTo(1)
    }

    @Test
    fun `page for append uses integer division`() {
        assertThat(cardsPage(LoadType.APPEND, cachedCount = 0, pageSize = 32)).isEqualTo(1)
        assertThat(cardsPage(LoadType.APPEND, cachedCount = 32, pageSize = 32)).isEqualTo(2)
        assertThat(cardsPage(LoadType.APPEND, cachedCount = 33, pageSize = 32)).isEqualTo(2)
    }

    @Test
    fun `end of pagination when empty or count reaches total`() {
        assertThat(cardsEndOfPagination(empty = true, currentCount = 0, totalCount = 100)).isTrue()
        assertThat(cardsEndOfPagination(empty = false, currentCount = 100, totalCount = 100)).isTrue()
        assertThat(cardsEndOfPagination(empty = false, currentCount = 50, totalCount = 100)).isFalse()
    }
}
