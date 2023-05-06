include(":app")
include(":domain")
include(":data")
include(":presentation")
include(":features:musicservice")

pluginManagement {
    includeBuild("Plugins")
    repositories {
        gradlePluginPortal()
        mavenCentral()
        google()
    }
}
