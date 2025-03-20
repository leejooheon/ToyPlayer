include(":app")

//include(":features:musicplayer")
include(":features:player")
include(":features:musicservice")
include(":features:common-ui")
include(":features:common")
include(":features:main")
include(":features:settings")
include(":features:splash")
include(":features:playlist")
include(":features:library")
include(":features:artist")
include(":features:album")

include(":core:network")
include(":core:resources")
include(":core:designsystem")
include(":core:navigation")

include(":domain:repository-api")
include(":domain:model")
include(":domain:usecase")

include(":data:datastore")
include(":data:music")
include(":data:playlist")
include(":data:repository")
include(":data:api")

pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
        google()
        includeBuild("build-logic")
    }
}
