plugins {
    alias(libs.plugins.poketcg.android.library)
    alias(libs.plugins.poketcg.android.library.compose)
 }

android {
    namespace = "com.lfgtavora.poketcg.core.ui"
}

dependencies {
    api(project(":core:designsystem"))
}