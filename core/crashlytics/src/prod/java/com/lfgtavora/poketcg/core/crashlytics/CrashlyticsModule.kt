package com.lfgtavora.poketcg.core.crashlytics

import com.google.firebase.Firebase
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.crashlytics.crashlytics
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal abstract class CrashlyticsModule {
    @Binds
    abstract fun bindsCrashlyticsHelper(
        crashlyticsHelperImpl: FirebaseCrashlyticsHelper,
    ): CrashlyticsHelper

    companion object {
        @Provides
        @Singleton
        fun provideFirebaseCrashlytics(): FirebaseCrashlytics = Firebase.crashlytics
    }
}
