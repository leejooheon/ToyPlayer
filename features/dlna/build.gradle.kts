import com.jooheon.toyplayer.setNamespace

plugins {
    id("toyplayer.android.feature")
}

android {
    setNamespace("features.dlna")
}

dependencies {
    implementation(projects.core.system)
    implementation(projects.domain.castApi)
    implementation(projects.features.musicservice)

    implementation(libs.androidx.media3.common)
}