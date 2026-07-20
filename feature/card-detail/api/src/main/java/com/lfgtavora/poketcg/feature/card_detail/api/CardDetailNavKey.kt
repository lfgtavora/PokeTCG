package com.lfgtavora.poketcg.feature.card_detail.api

import androidx.navigation3.runtime.NavKey
import com.lfgtavora.poketcg.core.navigation.Navigator
import kotlinx.serialization.Serializable

@Serializable
data class CardDetailNavKey(val id: String) : NavKey

fun Navigator.navigateToCardDetail(id: String) {
    navigate(CardDetailNavKey(id))
}