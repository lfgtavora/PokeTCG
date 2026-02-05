package com.lfgtavora.poketcg.database.di

import com.lfgtavora.poketcg.database.PokeTcgDatabase
import com.lfgtavora.poketcg.database.dao.CardDao
import com.lfgtavora.poketcg.database.dao.SetDao
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


}