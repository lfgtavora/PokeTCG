import org.gradle.kotlin.dsl.libs

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

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    testImplementation(libs.junit)
    api(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    implementation(libs.androidx.room.paging)
    implementation(libs.kotlinx.serialization.json)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}