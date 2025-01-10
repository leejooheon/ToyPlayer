import com.jooheon.toyplayer.setNamespace

plugins {
    id("toyplayer.android.library")
    id("toyplayer.android.hilt")
    alias(libs.plugins.ksp)
}

android {
    setNamespace("data")
}

dependencies {
    implementation(projects.domain)
    implementation(projects.core.strings)

    implementation(projects.data.datastore)
    implementation(projects.data.music)
    implementation(projects.data.playlist)
    implementation(projects.data.system)

    implementation(libs.kotlinx.serialization.json)
}