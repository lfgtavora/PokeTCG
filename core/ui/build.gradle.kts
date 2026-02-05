plugins {
    alias(libs.plugins.poketcg.android.library)
    alias(libs.plugins.poketcg.android.library.compose)
 }

android {
    namespace = "com.lfgtavora.poketcg.core.ui"
}

dependencies {
    implementation(libs.androidx.core.ktx)
    api(project(":core:designsystem"))

    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}