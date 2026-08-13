package com.lfgtavora.poketcg

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.layout
import androidx.compose.ui.zIndex
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.lfgtavora.poketcg.core.navigation.Navigator
import com.lfgtavora.poketcg.feature.card_detail.api.navigateToCardDetail
import com.lfgtavora.poketcg.feature.home.impl.ui.HomeScreen
import com.lfgtavora.poketcg.feature.sets.api.navigateToSetDetail
import com.lfgtavora.poketcg.search.impl.ui.SearchScreen

@Composable
fun MainScreen(
    rootNavigator: Navigator
) {
    var currentTab by rememberSaveable { mutableStateOf(AppDestinations.HOME) }
    // Keep tabs alive after first visit so switching doesn't recreate them
    var visitedTabs by rememberSaveable {
        mutableStateOf(setOf(AppDestinations.HOME))
    }

    LaunchedEffect(currentTab) {
        if (currentTab !in visitedTabs) {
            visitedTabs = visitedTabs + currentTab
        }
    }

    NavigationSuiteScaffold(
        navigationSuiteItems = {
            AppDestinations.entries.forEach { tab ->
                item(
                    icon = {
                        Icon(
                            tab.icon,
                            contentDescription = tab.label
                        )
                    },
                    label = { Text(tab.label) },
                    selected = tab == currentTab,
                    onClick = { currentTab = tab },
                )
            }
        }
    ) {

        Scaffold(
            modifier = Modifier.fillMaxSize(),

        ) { padding ->
            Box(
                Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .consumeWindowInsets(padding)
                    .windowInsetsPadding(
                        WindowInsets.safeDrawing.only(
                            WindowInsetsSides.Horizontal,
                        ),
                    ),
            ) {
                AppDestinations.entries.forEach { tab ->
                    if (tab in visitedTabs) {
                        key(tab) {
                            val selected = tab == currentTab
                            Box(
                                Modifier
                                    .fillMaxSize()
                                    .zIndex(if (selected) 1f else 0f)
                                    .then(if (selected) Modifier else Modifier.invisible())
                            ) {
                                when (tab) {
                                    AppDestinations.HOME -> HomeScreen(
                                        onSetClick = { setId ->
                                            rootNavigator.navigateToSetDetail(setId)
                                        }
                                    )

                                    AppDestinations.SEARCH -> SearchScreen(
                                        onCardClick = { cardId ->
                                            rootNavigator.navigateToCardDetail(cardId)
                                        }
                                    )

                                    AppDestinations.FAVORITES -> {
                                        Column(
                                            modifier = Modifier.fillMaxSize(),
                                            verticalArrangement = Arrangement.Center,
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            Text(text = " Under construction")
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

/** Measure + keep state, but don't draw or receive input. */
private fun Modifier.invisible(): Modifier =
    this
        .then(
            Modifier
                .layout { measurable, constraints ->
                    val placeable = measurable.measure(constraints)
                    layout(placeable.width, placeable.height) {
                    }
                }
        )

fun EntryProviderScope<NavKey>.mainScreenEntry(navigator: Navigator) {
    entry<MainScreenKey> {
        MainScreen(
           rootNavigator = navigator
        )
    }
}

enum class AppDestinations(
    val label: String,
    val icon: ImageVector,
) {
    HOME("Home", Icons.Default.Home),
    SEARCH("Search", Icons.Default.Search),
    FAVORITES("Favorites", Icons.Default.Favorite),
}