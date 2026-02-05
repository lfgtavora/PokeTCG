plugins {
    alias(libs.plugins.poketcg.android.library)
    alias(libs.plugins.poketcg.hilt)
    id("kotlinx-serialization")
}

android {
    namespace = "com.lfgtavora.poketcg.network"
    compileSdk {
        version = release(36)
    }

    buildFeatures {
        buildConfig = true
    }
}

dependencies {
    implementation(libs.okhttp3.loggingInterceptor)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    implementation(libs.kotlinx.serialization.json)
    ksp(libs.androidx.room.compiler)
    implementation(libs.retrofit)
    implementation(libs.retrofit.kotlinx.serialization)
    implementation(libs.converter.gson)
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

}
