package com.lfgtavora.poketcg.data.di

import com.lfgtavora.poketcg.data.repository.CardRepository
import com.lfgtavora.poketcg.data.repository.OfflineFirstCardRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent // Ou outro componente

@Module
@InstallIn(ViewModelComponent::class)
abstract class CardModule {
    @Binds
    abstract fun bindCardRepository(
        cardRepository: OfflineFirstCardRepository
    ): CardRepository
}