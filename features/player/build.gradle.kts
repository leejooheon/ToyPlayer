import com.jooheon.toyplayer.setNamespace

plugins {
    id("toyplayer.android.feature")
}

android {
    setNamespace("features.player")
}

dependencies {
    implementation(projects.features.musicservice)
    implementation(libs.androidx.constraintlayout.compose)

    implementation(libs.androidx.media3.common)

    implementation(libs.bumptech.glide.compose)
    implementation(libs.airbnb.android.lottie.compose)
    implementation(libs.tedpermission.coroutine)
}