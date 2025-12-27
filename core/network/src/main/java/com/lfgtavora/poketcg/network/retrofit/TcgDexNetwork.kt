package com.lfgtavora.poketcg.network.retrofit

import com.lfgtavora.poketcg.network.TcgDexNetworkDataSource
import com.lfgtavora.poketcg.network.model.SetBriefResponse
import com.lfgtavora.poketcg.network.model.SetResponse
import kotlinx.serialization.json.Json
import okhttp3.Call
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import javax.inject.Inject

private interface TcgDexNetworkApi {
    @GET("sets")
    suspend fun getSets(
        @Query("pagination:page") page: Int,
        @Query("pagination:itemsPerPage") itemsPerPage: Int,
        @Query("sort:order") orderBy: String?,
        @Query("sort:field") field: String?
    ): List<SetBriefResponse>

    @GET("sets/{id}")
    suspend fun getSet(id: String): SetResponse


}

private const val BASE_URL = "https://api.tcgdex.net/v2/en/"

internal class TcgDexNetwork @Inject constructor(
    networkJson: Json,
    okhttpCallFactory: dagger.Lazy<Call.Factory>,
) : TcgDexNetworkDataSource {

    private val networkApi: TcgDexNetworkApi by lazy {
        Retrofit.Builder()
            .callFactory { okhttpCallFactory.get().newCall(it) }
            .addConverterFactory(
                networkJson.asConverterFactory(
                    "application/json;".toMediaType()
                )
            )
            .baseUrl(BASE_URL)
            .build()
            .create(TcgDexNetworkApi::class.java)
    }

    override suspend fun getAllSets(): List<SetBriefResponse> {
        TODO("Not yet implemented")
    }

    override suspend fun getSets(
        page: Int,
        itemsPerPage: Int,
        orderBy: String?,
        field: String?
    ): List<SetBriefResponse> =
        networkApi.getSets(page, itemsPerPage, orderBy, field)


    override suspend fun getSet(id: String): SetResponse {
        TODO("Not yet implemented")
    }

    override suspend fun getCard(id: String): Any {
        TODO("Not yet implemented")
    }

}
