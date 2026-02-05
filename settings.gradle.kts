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
include(":app")
include(":core:ui")
include(":core:network")
include(":core:database")
include(":core:model")
include(":core:data")
include(":feature:home:api")
include(":feature:home:impl")
include(":core:navigation")
include(":core:designsystem")
include(":feature:setdetail")
include(":feature:sets:api")
include(":feature:sets:impl")
