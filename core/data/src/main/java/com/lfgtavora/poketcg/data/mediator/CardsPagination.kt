package com.lfgtavora.poketcg.data.mediator

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.RemoteMediator

@OptIn(ExperimentalPagingApi::class)
internal fun cardsInitializeAction(cachedCount: Int): RemoteMediator.InitializeAction =
    if (cachedCount > 0) {
        RemoteMediator.InitializeAction.SKIP_INITIAL_REFRESH
    } else {
        RemoteMediator.InitializeAction.LAUNCH_INITIAL_REFRESH
    }

/**
 * Returns the page to fetch, or null when pagination should end without a network call.
 */
internal fun cardsPage(loadType: LoadType, cachedCount: Int, pageSize: Int): Int? =
    when (loadType) {
        LoadType.REFRESH -> 1
        LoadType.PREPEND -> null
        LoadType.APPEND -> if (cachedCount == 0) 1 else (cachedCount / pageSize) + 1
    }

internal fun cardsEndOfPagination(
    empty: Boolean,
    currentCount: Int,
    totalCount: Int,
): Boolean = empty || currentCount >= totalCount
