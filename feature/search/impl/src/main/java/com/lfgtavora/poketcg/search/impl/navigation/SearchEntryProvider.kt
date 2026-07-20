package com.lfgtavora.poketcg.search.impl.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.lfgtavora.poketcg.core.navigation.Navigator
import com.lfgtavora.poketcg.feature.card_detail.api.navigateToCardDetail
import com.lfgtavora.poketcg.feature.search.api.SearchNavKey
import com.lfgtavora.poketcg.search.impl.SearchScreen


fun EntryProviderScope<NavKey>.searchEntry(navigator: Navigator) {
    entry<SearchNavKey> {
        SearchScreen(
           onCardClick = navigator::navigateToCardDetail
        )
    }
}