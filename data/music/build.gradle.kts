import com.jooheon.toyplayer.setNamespace

plugins {
    id("toyplayer.android.library")
}

android {
    setNamespace("data.music")
}

dependencies {
    implementation(projects.data.api)
    implementation(projects.domain.model)
    implementation(projects.core.resources)

    implementation(libs.androidx.core.ktx)
    implementation(libs.javax.inject)
    implementation(libs.androidx.media3.common)
    implementation(libs.jakewharton.timber)
}