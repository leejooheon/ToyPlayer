import com.jooheon.toyplayer.setNamespace

plugins {
    id("toyplayer.android.library")
    id("toyplayer.android.compose")
}

android {
    setNamespace("core.designsystem")
}

dependencies {
    implementation(projects.core.resources)
    implementation(projects.domain.model)
}