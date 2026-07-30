package com.lfgtavora.poketcg.database.di

import com.lfgtavora.poketcg.database.PokeTcgDatabase
import com.lfgtavora.poketcg.database.dao.CardDao
import com.lfgtavora.poketcg.database.dao.CardRemoteKeyDao
import com.lfgtavora.poketcg.database.dao.SetDao
import com.lfgtavora.poketcg.database.dao.SetRemoteKeyDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object DaoModule {
    @Provides
    fun providesSetDao(
        database: PokeTcgDatabase,
    ): SetDao = database.setDao()

    @Provides
    fun providesCardDao(
        database: PokeTcgDatabase,
    ): CardDao = database.cardDao()

    @Provides
    fun providesSetRemoteKeyDao(
        database: PokeTcgDatabase,
    ): SetRemoteKeyDao = database.setRemoteKeyDao()

    @Provides
    fun providesCardRemoteKeyDao(
        database: PokeTcgDatabase,
    ): CardRemoteKeyDao = database.cardRemoteKeyDao()
}