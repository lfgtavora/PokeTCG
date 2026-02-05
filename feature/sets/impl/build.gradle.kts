plugins {
    alias(libs.plugins.poketcg.android.feature.impl)
    alias(libs.plugins.poketcg.android.library.compose)
}

android {
    namespace = "com.lfgtavora.poketcg.feature.sets.impl"
}

dependencies {
    implementation(project(":core:data"))
    implementation(project(":feature:sets:api"))

    implementation(libs.androidx.compose.material3.adaptive.navigation3)

    implementation(libs.androidx.paging.runtime)
    implementation(libs.androidx.paging.compose)

}