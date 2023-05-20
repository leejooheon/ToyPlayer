include(":app")
include(":domain")
include(":data")
include(":presentation")
include(":features:musicservice")
include(":features:musicplayer")
include(":features:github")
include(":features:wikipedia")
include(":features:map")
include(":features:common")
include(":features:main")

pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
        google()
        includeBuild("Plugins")
    }
}
