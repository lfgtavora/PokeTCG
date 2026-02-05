package com.lfgtavora.poketcg.feature.sets.impl.navigation

import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.navigation3.ListDetailSceneStrategy
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.lfgtavora.poketcg.core.navigation.Navigator
import com.lfgtavora.poketcg.feature.sets.api.SetsDetailNavKey
import com.lfgtavora.poketcg.feature.sets.impl.SetsDetailsScreen
import com.lfgtavora.poketcg.feature.sets.impl.SetsDetailsViewModel

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
fun EntryProviderScope<NavKey>.setsDetailEntry(navigator: Navigator) {
    entry<SetsDetailNavKey>(
        metadata = ListDetailSceneStrategy.detailPane(),
    ) { key ->
        val id = key.id
        val viewModel = hiltViewModel<SetsDetailsViewModel, SetsDetailsViewModel.Factory>(
            key = id,
        ) { factory ->
            factory.create(setId = id)
        }

        SetsDetailsScreen(
            viewModel = viewModel,
            onBack = { navigator.goBack() }
        )
    }


}