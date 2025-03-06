import com.jooheon.toyplayer.setNamespace

plugins {
    id("toyplayer.android.library")
}

android {
    setNamespace("data.music")
}

dependencies {
    implementation(projects.domain.model)
    implementation(projects.core.strings)

    implementation(libs.javax.inject)
    implementation(libs.androidx.media3.common)
}