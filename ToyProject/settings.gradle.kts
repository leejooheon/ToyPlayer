include(":app")
include(":domain")
include(":data")
include(":presentation")
include(":features:musicservice")
include(":features:musicplayer")
include(":features:common")

pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
        google()
        includeBuild("Plugins")
    }
}
