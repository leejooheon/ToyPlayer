include(":app")
include(":domain")
include(":data")
include(":features:musicservice")
include(":features:musicplayer")
include(":features:common")
include(":features:main")
include(":features:setting")
include(":features:splash")
//include(":features:presentation")
//include(":features:github")
//include(":features:wikipedia")
//include(":features:map")
//include(":features:widget")

pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
        google()
        includeBuild("build-logic")
    }
}
