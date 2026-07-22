package com.lfgtavora.poketcg

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.navigation3.rememberListDetailSceneStrategy
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTagsAsResourceId
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.lfgtavora.poketcg.core.navigation.Navigator
import com.lfgtavora.poketcg.core.navigation.rememberNavigationState
import com.lfgtavora.poketcg.core.navigation.toEntries
import com.lfgtavora.poketcg.feature.card_detail.impl.cardDetailEntry
import com.lfgtavora.poketcg.feature.home.api.HomeNavKey
import com.lfgtavora.poketcg.feature.home.impl.navigation.homeEntry
import com.lfgtavora.poketcg.feature.sets.impl.navigation.setsDetailEntry
import com.lfgtavora.poketcg.search.impl.navigation.searchEntry
import com.lfgtavora.poketcg.ui.theme.PokeTCGTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PokeTCGTheme {
                PokeTCGApp()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@PreviewScreenSizes
@Composable
fun PokeTCGApp() {
    val navigationState = rememberNavigationState(MainScreenKey, setOf(MainScreenKey))
    val listDetailStrategy = rememberListDetailSceneStrategy<NavKey>()
    val navigator = Navigator(navigationState)

    val entries = entryProvider {
        mainScreenEntry(navigator)
        homeEntry(navigator)
        setsDetailEntry(navigator)
        cardDetailEntry(navigator)
        searchEntry(navigator)
    }

    NavDisplay(
        entries = navigationState.toEntries(entries),
        sceneStrategy = listDetailStrategy,
        modifier = Modifier.semantics {
            testTagsAsResourceId = true
        },
        onBack = { navigator.goBack() },
    )

}
