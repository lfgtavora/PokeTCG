package com.lfgtavora.poketcg.data.mediator

import androidx.paging.ExperimentalPagingApi
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
}
