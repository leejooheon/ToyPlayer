import com.jooheon.toyplayer.setNamespace

plugins {
    id("toyplayer.android.library")
    id("toyplayer.android.hilt")
    alias(libs.plugins.ksp)
}

android {
    setNamespace("data.repository")
}

dependencies {
    implementation(projects.domain.model)
    implementation(projects.domain.repositoryApi)

    implementation(projects.core.resources)
    implementation(projects.core.network)

    implementation(projects.data.datastore)
    implementation(projects.data.music)
    implementation(projects.data.playlist)
    implementation(projects.data.equalizer)

    implementation(libs.kotlinx.serialization.json)
}