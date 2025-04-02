pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
        google()
        includeBuild("build-logic")
    }
}

@Suppress("UnstableApiUsage")
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven(url = "https://jitpack.io" )
    }
}

include(":app")

include(
    ":features:player",
    ":features:musicservice",
    ":features:common-ui",
    ":features:common",
    ":features:main",
    ":features:settings",
    ":features:splash",
    ":features:playlist",
    ":features:library",
    ":features:artist",
    ":features:album"
)

include(
    ":core:network",
    ":core:resources",
    ":core:designsystem",
    ":core:navigation"
)

include(
    ":domain:repository-api",
    ":domain:model",
    ":domain:usecase"
)

include(
    ":data:datastore",
    ":data:music",
    ":data:playlist",
    ":data:repository",
    ":data:api",
    ":data:equalizer"
)