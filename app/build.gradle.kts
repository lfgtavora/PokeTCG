plugins {
    alias(libs.plugins.poketcg.hilt)
    alias(libs.plugins.poketcg.android.application)
    alias(libs.plugins.poketcg.android.application.compose)
    id("org.jetbrains.kotlin.plugin.serialization")

}

android {
    namespace = "com.lfgtavora.poketcg"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        applicationId = "com.lfgtavora.poketcg"
        minSdk = 28
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation(project(":core:network"))
    implementation(project(":core:navigation"))
    implementation(project(":feature:home:api"))
    implementation(project(":feature:home:impl"))
    implementation(project(":feature:sets:api"))
    implementation(project(":feature:sets:impl"))
    implementation(project(":feature:card-detail:api"))
    implementation(project(":feature:card-detail:impl"))
    implementation(project(":feature:search:impl"))
    implementation(project(":feature:search:api"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material3.adaptive.navigation.suite)
    implementation(libs.compose.material.icons)
    implementation(libs.androidx.splashscreen)
    implementation(libs.hilt.android)

    implementation(libs.androidx.compose.material3.adaptive.navigation3)
    implementation(libs.androidx.navigation3.ui)
    implementation(libs.androidx.lifecycle.runtimeCompose)
    implementation(libs.androidx.lifecycle.viewModel.navigation3)

    //ksp(libs.kotlin.metadata.jvm)

    ksp(libs.hilt.compiler)

    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}