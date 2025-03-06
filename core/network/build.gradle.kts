import com.jooheon.toyplayer.setNamespace

plugins {
    id("toyplayer.android.library")
    id("toyplayer.android.hilt")
}

android {
    setNamespace("core.network")
}

dependencies {
    implementation(libs.javax.inject)
    implementation(projects.domain.usecase)
}