include(":app")

include(":features:musicservice")
include(":features:musicplayer")
include(":features:common")
include(":features:main")
include(":features:setting")
include(":features:splash")

include(":core:network")
include(":core:strings")
include(":core:designsystem")
include(":core:navigation")

include(":domain:repository-api")
include(":domain:model")
include(":domain:usecase")

include(":data")
include(":data:datastore")
include(":data:music")
include(":data:playlist")
include(":data:repository")

pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
        google()
        includeBuild("build-logic")
    }
}
