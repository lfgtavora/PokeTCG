package com.lfgtavora.poketcg.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SearchDataResponse(
    val data: List<SearchItemResponse>
)

@Serializable
sealed interface SearchItemResponse {
    @Serializable
    @SerialName("card")
    data class Card(
        val id: String,
        val name: String,
        val set: SearchSetInfo,
        val images: SearchCardImages? = null,
    ) : SearchItemResponse

    @Serializable
    @SerialName("set")
    data class Set(
        val id: String,
        val name: String,
        val series: String? = null,
        val images: SearchSetImages? = null,
    ) : SearchItemResponse
}

@Serializable
data class SearchSetInfo(
    val id: String,
    val name: String,
)

@Serializable
data class SearchCardImages(
    val small: String? = null,
    val large: String? = null,
)

@Serializable
data class SearchSetImages(
    val symbol: String? = null,
    val logo: String? = null,
)
