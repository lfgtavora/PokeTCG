package com.lfgtavora.poketcg.network.retrofit

import com.lfgtavora.poketcg.network.TcgDexNetworkDataSource
import com.lfgtavora.poketcg.network.model.CardDataListResponse
import com.lfgtavora.poketcg.network.model.CardDataResponse
import com.lfgtavora.poketcg.network.model.SearchDataResponse
import com.lfgtavora.poketcg.network.model.SetDataListResponse
import com.lfgtavora.poketcg.network.model.SetResponse
import kotlinx.serialization.json.Json
import okhttp3.Call
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import javax.inject.Inject
import javax.inject.Singleton

private interface TcgDexNetworkApi {
    @GET("sets")
    suspend fun getSets(
        @Query("page") page: Int,
        @Query("pageSize") itemsPerPage: Int,
        @Query("orderBy") orderBy: String? = null,
    ): SetDataListResponse

    @GET("sets/{id}")
    suspend fun getSet(id: String): SetResponse

    @GET("cards")
    suspend fun getCards(
        @Query("q") query: String,
        @Query("page") page: Int,
        @Query("pageSize") itemsPerPage: Int,
        @Query("select") select: String?,
    ): CardDataListResponse

    @GET("cards/{id}")
    suspend fun getCard(
        @Path("id")
        id: String
    ): CardDataResponse

    @GET("search")
    suspend fun search(
        @Query("query") query: String,
        @Query("types") types: String,
    ): SearchDataResponse
}

private const val BASE_URL = "http://10.0.2.2:8080/v2/"

@Singleton
internal class TcgDexNetwork @Inject constructor(
    networkJson: Json,
    okhttpCallFactory: dagger.Lazy<Call.Factory>,
) : TcgDexNetworkDataSource {

    private val networkApi: TcgDexNetworkApi by lazy {
        Retrofit.Builder()
            .callFactory { okhttpCallFactory.get().newCall(it) }
            .addConverterFactory(
                networkJson.asConverterFactory(
                    "application/json".toMediaType()
                )
            )
            .baseUrl(BASE_URL)
            .build()
            .create(TcgDexNetworkApi::class.java)
    }

    override suspend fun getAllSets(): List<SetResponse> {
        TODO("Not yet implemented")
    }

    override suspend fun getSetsBrief(
        page: Int,
        pageSize: Int,
        orderBy: String?,
        field: String?
    ): List<SetResponse> =
        networkApi.getSets(page, pageSize, orderBy).data

    override suspend fun getSet(id: String): SetResponse =
        networkApi.getSet(id)

    override suspend fun getCard(id: String): CardDataResponse =
        networkApi.getCard(id)

    override suspend fun getCards(
        query: String,
        page: Int,
        pageSize: Int,
        select: String?
    ): CardDataListResponse =
        networkApi.getCards(
            query = query,
            page = page,
            itemsPerPage = pageSize,
            select
        )

    override suspend fun search(
        query: String,
        types: String,
    ): SearchDataResponse =
        networkApi.search(query = query, types = types)
}
