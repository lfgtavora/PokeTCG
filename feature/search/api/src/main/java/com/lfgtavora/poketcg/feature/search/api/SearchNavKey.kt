package com.lfgtavora.poketcg.feature.search.api

import androidx.navigation3.runtime.NavKey
import com.lfgtavora.poketcg.core.navigation.Navigator
import kotlinx.serialization.Serializable

@Serializable
object SearchNavKey: NavKey {

}

fun Navigator.navigateToSearch() {
    navigate(SearchNavKey)
}