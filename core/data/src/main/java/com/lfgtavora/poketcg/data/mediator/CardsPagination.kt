package com.lfgtavora.poketcg.data.mediator

import androidx.paging.ExperimentalPagingApi
import androidx.paging.RemoteMediator

@OptIn(ExperimentalPagingApi::class)
internal fun cardsInitializeAction(cachedCount: Int): RemoteMediator.InitializeAction =
    if (cachedCount > 0) {
        RemoteMediator.InitializeAction.SKIP_INITIAL_REFRESH
    } else {
        RemoteMediator.InitializeAction.LAUNCH_INITIAL_REFRESH
    }
