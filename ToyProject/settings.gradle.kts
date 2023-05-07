include(":app")
include(":domain")
include(":data")
include(":presentation")
include(":features:musicservice")
include(":features:common")

pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
        google()
        includeBuild("Plugins")
    }
}