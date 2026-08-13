pluginManagement {
    includeBuild("build-logic")
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "PokeTCG"
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

include(":app")

include(":core:ui")
include(":core:network")
include(":core:database")
include(":core:model")
include(":core:data")
include(":core:navigation")
include(":core:designsystem")
include(":core:common")
include(":core:analytics")

include(":feature:home:api")
include(":feature:home:impl")
include(":feature:sets:api")
include(":feature:sets:impl")
include(":feature:card-detail:api")
include(":feature:card-detail:impl")
include(":feature:search:api")
include(":feature:search:impl")

include(":core:crashlytics")
