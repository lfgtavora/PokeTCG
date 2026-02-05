package com.lfgtavora.poketcg.network.di

import android.R.attr.level
import com.lfgtavora.poketcg.network.TcgDexNetworkDataSource
import com.lfgtavora.poketcg.network.retrofit.TcgDexNetwork
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.Call
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object NetworkModule {

    @Provides
    @Singleton
    fun providesNetworkJson(): Json = Json {
        ignoreUnknownKeys = true
    }

    @Provides
    @Singleton
    fun okHttpCallFactory(): Call.Factory {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        return OkHttpClient.Builder()
            .connectTimeout(1, java.util.concurrent.TimeUnit.MINUTES)
            .readTimeout(1, java.util.concurrent.TimeUnit.MINUTES)
            .writeTimeout(1, java.util.concurrent.TimeUnit.MINUTES)
            .addInterceptor(loggingInterceptor)
            .addInterceptor { chain ->
                val request = chain.request().newBuilder()
                    .header("X-Api-Key", "d5676117-c38c-48db-95a8-76940e8799d2")
                    .build()
                chain.proceed(request)
            }
            .build()
    }


    @Provides
    @Singleton
    fun bindTcgDexNetworkDataSource(
        impl: TcgDexNetwork // A implementação real da sua rede
    ): TcgDexNetworkDataSource = impl

}