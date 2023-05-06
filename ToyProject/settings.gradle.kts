include(":app")
include(":domain")
include(":data")
include(":presentation")
include(":features:musicservice")
include(":features:common")

pluginManagement {
    includeBuild("Plugins")
    repositories {
        gradlePluginPortal()
        mavenCentral()
        google()
    }
}
