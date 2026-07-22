plugins {
    alias(libs.plugins.poketcg.android.library)
    alias(libs.plugins.poketcg.hilt)
    alias(libs.plugins.hilt)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.poketcg.android.library.compose)
}

android {
    namespace = "com.lfgtavora.poketcg.core.navigation"
}
dependencies {
    api(libs.androidx.navigation3.runtime)
    implementation(libs.androidx.savedstate.compose)
    implementation(libs.androidx.lifecycle.viewModel.navigation3)
}