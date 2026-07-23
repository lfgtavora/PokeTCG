package com.lfgtavora.poketcg.feature.sets.impl.navigation

import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.navigation3.ListDetailSceneStrategy
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.lfgtavora.poketcg.core.navigation.Navigator
import com.lfgtavora.poketcg.feature.card_detail.api.CardDetailNavKey
import com.lfgtavora.poketcg.feature.sets.api.SetsDetailNavKey
import com.lfgtavora.poketcg.feature.sets.impl.ui.SetsDetailsScreen
import com.lfgtavora.poketcg.feature.sets.impl.ui.SetsDetailsViewModel

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
fun EntryProviderScope<NavKey>.setsDetailEntry(navigator: Navigator) {
    entry<SetsDetailNavKey>(
        metadata = ListDetailSceneStrategy.listPane(),
    ) { key ->
        val id = key.id
        val viewModel = hiltViewModel<SetsDetailsViewModel, SetsDetailsViewModel.Factory>(
            key = id,
        ) { factory ->
            factory.create(setId = id)
        }

        SetsDetailsScreen(
            viewModel = viewModel,
            onItemClick = { cardId -> navigator.navigate(CardDetailNavKey(cardId)) },
            onBack = { navigator.goBack() }
        )
    }


}