package com.lfgtavora.poketcg

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
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.lfgtavora.poketcg.core.navigation.Navigator
import com.lfgtavora.poketcg.feature.card_detail.api.navigateToCardDetail
import com.lfgtavora.poketcg.feature.home.impl.HomeScreen
import com.lfgtavora.poketcg.feature.search.api.navigateToSearch
import com.lfgtavora.poketcg.feature.sets.api.navigateToSetDetail
import com.lfgtavora.poketcg.search.impl.SearchScreen
import kotlinx.serialization.Serializable

@Composable
fun MainScreen(
    rootNavigator: Navigator
) {
    var currentTab by rememberSaveable { mutableStateOf(AppDestinations.HOME) }

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
            Column(
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
                when (currentTab) {
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

                    AppDestinations.PROFILE -> {
                        Text(text = "Profile")
                    }

                    else -> {}
                }
            }
        }
    }
}

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
    PROFILE("Profile", Icons.Default.AccountBox),
}