include(":app")
include(":domain")
include(":data")
include(":features:musicservice")
include(":features:musicplayer")
include(":features:common")
include(":features:main")
include(":features:setting")
include(":features:splash")

include(":core:strings")
include(":core:designsystem")
include(":core:navigation")

//include(":testing")

pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
        google()
        includeBuild("build-logic")
    }
}
