plugins {
    alias(libs.plugins.poketcg.android.library)
    alias(libs.plugins.poketcg.android.room)
    alias(libs.plugins.poketcg.hilt)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.lfgtavora.poketcg.database"
}

dependencies {
    api(project(":core:model"))

    implementation(libs.androidx.room.paging)
    implementation(libs.kotlinx.serialization.json)
}