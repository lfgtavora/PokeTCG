package com.lfgtavora.poketcg.build_logic.convention

import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.ApplicationProductFlavor
import com.android.build.api.dsl.CommonExtension

enum class PokeTcgFlavor(val applicationIdSuffix: String? = null) {
    demo(".demo"),
    prod,
}

fun configureFlavors(commonExtension: CommonExtension<*, *, *, *, *, *>) {
    commonExtension.apply {
        flavorDimensions += "tier"
        productFlavors {
            PokeTcgFlavor.values().forEach { flavor ->
                create(flavor.name) {
                    dimension = "tier"
                    if (this@apply is ApplicationExtension && this is ApplicationProductFlavor) {
                        flavor.applicationIdSuffix?.let { applicationIdSuffix = it }
                    }
                }
            }
        }
    }
}
