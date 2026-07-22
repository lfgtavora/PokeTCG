import java.util.Properties

plugins {
    alias(libs.plugins.poketcg.android.library)
    alias(libs.plugins.poketcg.hilt)
    id("kotlinx-serialization")
}

val localProperties = Properties().apply {
    val localFile = rootProject.file("local.properties")
    if (localFile.exists()) {
        localFile.inputStream().use(::load)
    }
}

val tcgBaseUrl: String =
    localProperties.getProperty("TCG_BASE_URL")
        ?: providers.environmentVariable("TCG_BASE_URL").orNull
        ?: "http://10.0.2.2:8080/v2/"

android {
    namespace = "com.lfgtavora.poketcg.network"

    buildFeatures {
        buildConfig = true
    }

    defaultConfig {
        buildConfigField("String", "TCG_BASE_URL", "\"$tcgBaseUrl\"")
    }
}

dependencies {
    implementation(libs.okhttp3.loggingInterceptor)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.retrofit)
    implementation(libs.retrofit.kotlinx.serialization)
}
