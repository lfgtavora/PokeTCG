import com.google.firebase.crashlytics.buildtools.gradle.CrashlyticsExtension

plugins {
    alias(libs.plugins.poketcg.hilt)
    alias(libs.plugins.poketcg.android.application)
    alias(libs.plugins.poketcg.android.application.compose)
    id("org.jetbrains.kotlin.plugin.serialization")
    alias(libs.plugins.google.services)
    alias(libs.plugins.firebase.crashlytics)
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
        debug {
            configure<CrashlyticsExtension> {
                mappingFileUploadEnabled = false
            }
        }
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        create("releaseLocal") {
            initWith(getByName("release"))
            matchingFallbacks += listOf("release")
            signingConfig = signingConfigs.getByName("debug")
            configure<CrashlyticsExtension> {
                mappingFileUploadEnabled = false
            }
        }
    }
    productFlavors {
        named("demo") {
            configure<CrashlyticsExtension> {
                mappingFileUploadEnabled = false
            }
        }
    }
    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation(projects.core.analytics)
    implementation(projects.core.crashlytics)
    implementation(projects.core.network)
    implementation(projects.core.navigation)

    implementation(projects.feature.home.api)
    implementation(projects.feature.home.impl)
    implementation(projects.feature.sets.api)
    implementation(projects.feature.sets.impl)
    implementation(projects.feature.cardDetail.api)
    implementation(projects.feature.cardDetail.impl)
    implementation(projects.feature.search.impl)
    implementation(projects.feature.search.api)


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