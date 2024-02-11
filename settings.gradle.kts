include(":app")
include(":domain")
include(":data")
include(":testing")
include(":features:musicservice")
include(":features:musicplayer")
include(":features:common")
include(":features:main")
include(":features:setting")
include(":features:splash")
include(":features:strings")

pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
        google()
        includeBuild("build-logic")
    }
}
