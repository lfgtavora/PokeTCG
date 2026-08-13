plugins {
    alias(libs.plugins.poketcg.android.library)
    alias(libs.plugins.poketcg.hilt)
    id("kotlinx-serialization")
}

android {
    namespace = "com.lfgtavora.poketcg.data"
    testOptions.unitTests.isIncludeAndroidResources = true
}

dependencies {
    api(project(":core:model"))
    api(project(":core:common"))
    implementation(project(":core:database"))
    implementation(project(":core:network"))
    implementation(libs.androidx.room.runtime)
    ksp(libs.hilt.compiler)
    implementation(libs.androidx.paging.runtime)
}