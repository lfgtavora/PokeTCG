plugins {
    alias(libs.plugins.poketcg.android.feature.impl)
    alias(libs.plugins.poketcg.android.library.compose)
}

android {
    namespace = "com.lfgtavora.poketcg.feature.home.impl"
}

dependencies {
    implementation(project(":core:data"))
    implementation(project(":feature:home:api"))
    implementation(project(":feature:sets:api"))

    implementation(libs.coil.okhttp)

    implementation(libs.androidx.paging.runtime)
    implementation(libs.androidx.paging.compose)
}