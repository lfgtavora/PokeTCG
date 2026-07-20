plugins {
    alias(libs.plugins.poketcg.android.feature.impl)
    alias(libs.plugins.poketcg.android.library.compose)
}

android {
    namespace = "com.lfgtavora.poketcg.feature.card_detail.impl"
}

dependencies {
    implementation(project(":core:data"))
    implementation(project(":feature:card-detail:api"))

    implementation(libs.androidx.compose.material3.adaptive.navigation3)

}