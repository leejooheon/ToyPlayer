include(":app")
include(":domain")
include(":data")
//include(":features:presentation")
include(":features:musicservice")
include(":features:musicplayer")
include(":features:github")
include(":features:wikipedia")
include(":features:map")
include(":features:common")
include(":features:main")
include(":features:setting")
include(":features:splash")
include(":presentation")
//include(":features:widget")

pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
        google()
        includeBuild("Plugins")
    }
}
