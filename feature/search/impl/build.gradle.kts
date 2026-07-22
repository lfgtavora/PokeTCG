plugins {
    alias(libs.plugins.poketcg.android.feature.impl)
    alias(libs.plugins.poketcg.android.library.compose)
}

android {
    namespace = "com.lfgtavora.poketcg.feature.search.impl"
}

dependencies {
    implementation(project(":core:data"))
    implementation(project(":feature:search:api"))
    implementation(project(":feature:card-detail:api"))

}