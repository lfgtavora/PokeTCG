package com.lfgtavora.poketcg.feature.card_detail.impl.navigation

import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.navigation3.ListDetailSceneStrategy
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.lfgtavora.poketcg.core.navigation.Navigator
import com.lfgtavora.poketcg.feature.card_detail.api.CardDetailNavKey
import com.lfgtavora.poketcg.feature.card_detail.impl.ui.CardDetailScreen
import com.lfgtavora.poketcg.feature.card_detail.impl.ui.CardDetailViewModel

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
fun EntryProviderScope<NavKey>.cardDetailEntry(navigator: Navigator) {
    entry<CardDetailNavKey>(
        metadata = ListDetailSceneStrategy.detailPane(),
    ) { key ->
        val id = key.id
        val viewModel = hiltViewModel<CardDetailViewModel, CardDetailViewModel.Factory>(
            key = id,
        ) { factory ->
            factory.create(cardId = id)
        }

        CardDetailScreen(
            onBack = navigator::goBack,
            viewModel = viewModel
        )

    }
}