package com.lfgtavora.poketcg.feature.home.impl.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.lfgtavora.poketcg.core.navigation.Navigator
import com.lfgtavora.poketcg.feature.home.api.HomeNavKey
import com.lfgtavora.poketcg.feature.home.impl.ui.HomeScreen
import com.lfgtavora.poketcg.feature.sets.api.navigateToSetDetail

fun EntryProviderScope<NavKey>.homeEntry(navigator: Navigator) {
    entry<HomeNavKey> {
        HomeScreen(
            onSetClick = navigator::navigateToSetDetail
        )
    }
}