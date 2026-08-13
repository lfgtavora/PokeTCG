plugins {
    alias(libs.plugins.poketcg.android.library)
    alias(libs.plugins.poketcg.android.library.compose)
    alias(libs.plugins.poketcg.hilt)
}

android {
    namespace = "com.lfgtavora.poketcg.core.analytics"
}

dependencies {
    implementation(libs.androidx.compose.runtime)
//    prodImplementation(platform(libs.firebase.bom))
//    prodImplementation(libs.firebase.analytics)
}