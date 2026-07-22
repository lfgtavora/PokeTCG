plugins {
    alias(libs.plugins.poketcg.android.library)
    alias(libs.plugins.poketcg.hilt)
    id("kotlinx-serialization")
}

android {
    namespace = "com.lfgtavora.poketcg.network"

    buildFeatures {
        buildConfig = true
    }
}

dependencies {
    implementation(libs.okhttp3.loggingInterceptor)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.retrofit)
    implementation(libs.retrofit.kotlinx.serialization)
}
