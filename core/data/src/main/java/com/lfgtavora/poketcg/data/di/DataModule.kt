package com.lfgtavora.poketcg.data.di

import com.lfgtavora.poketcg.data.repository.CardRepository
import com.lfgtavora.poketcg.data.repository.OfflineFirstCardRepository
import com.lfgtavora.poketcg.data.repository.OfflineFirstSetRepository
import com.lfgtavora.poketcg.data.repository.SetRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {

    @Binds
    abstract fun bindSetRepository(
        setRepository: OfflineFirstSetRepository
    ): SetRepository



}