package com.lfgtavora.poketcg.core.crashlytics

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal abstract class CrashlyticsModule {
    @Binds
    abstract fun bindsCrashlyticsHelper(
        crashlyticsHelperImpl: StubCrashlyticsHelper,
    ): CrashlyticsHelper
}
