plugins {
    alias(libs.plugins.poketcg.android.feature.api)
}

android {
    namespace = "com.lfgtavora.poketcg.feature.home.api"
}

dependencies {
    api(libs.androidx.paging.common)
    api(libs.kotlinx.coroutines.core)
    api(project(":core:model"))
}