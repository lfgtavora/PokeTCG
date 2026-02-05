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
    api(project(":core:database"))
    api(project(":core:network"))

    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)

    implementation(libs.androidx.paging.runtime)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}