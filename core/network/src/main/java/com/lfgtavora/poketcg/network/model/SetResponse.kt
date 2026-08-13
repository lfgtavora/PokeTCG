package com.lfgtavora.poketcg.network.model

import kotlinx.serialization.Serializable

@Serializable
data class SetDataListResponse(
    val data: List<SetResponse>,
    val page: Int,
    val pageSize: Int,
    val count: Int,
    val totalCount: Int
)

@Serializable
data class SetDataResponse(
    val data: SetResponse
)

@Serializable
data class SetResponse(
    val id: String,
    val name: String? = null,
    val series: String? = null,
    val printedTotal: Int? = null,
    val total: Int? = null,
    val legalitiesResponse: LegalitiesResponse? = null,
    val ptcgoCode: String? = null,
    val releaseDate: String? = null,
    val updatedAt: String? = null,
    val images: SetImages? = null
)

@Serializable
data class SetImages(
    val symbol: String? = null,
    val logo: String? = null
)
