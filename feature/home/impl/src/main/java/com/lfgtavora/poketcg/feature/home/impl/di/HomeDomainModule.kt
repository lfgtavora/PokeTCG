package com.lfgtavora.poketcg.feature.home.impl.di

import com.lfgtavora.poketcg.feature.home.api.ObservePaginatedSetsPreviewByLastReleaseDateUseCase
import com.lfgtavora.poketcg.feature.home.impl.domain.ObservePaginatedSetsPreviewByLastReleaseDateUseCaseImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class HomeDomainModule {

    @Binds
    abstract fun bindObservePaginatedSetsPreviewByLastReleaseDateUseCase(
        impl: ObservePaginatedSetsPreviewByLastReleaseDateUseCaseImpl
    ): ObservePaginatedSetsPreviewByLastReleaseDateUseCase

}
