package com.lfgtavora.poketcg.feature.sets.api

import androidx.navigation3.runtime.NavKey
import com.lfgtavora.poketcg.core.navigation.Navigator
import kotlinx.serialization.Serializable

@Serializable
data class SetsDetailNavKey(val id: String) : NavKey

fun Navigator.navigateToSetDetail(id: String) {
    navigate(SetsDetailNavKey(id))
}