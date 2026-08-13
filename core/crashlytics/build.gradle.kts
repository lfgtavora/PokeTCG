plugins {
    alias(libs.plugins.poketcg.android.library)
    alias(libs.plugins.poketcg.hilt)
}

android {
    namespace = "com.lfgtavora.poketcg.core.crashlytics"
}

dependencies {
    prodImplementation(platform(libs.firebase.bom))
    prodImplementation(libs.firebase.crashlytics)
}
