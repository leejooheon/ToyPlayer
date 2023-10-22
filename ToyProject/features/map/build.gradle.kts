plugins {
    id("toyproject.android.feature")
}

android {
    namespace = "com.jooheon.clean_architecture.toyproject.features.map"
}

dependencies {
    implementation(libs.google.maps.compose)
    implementation(libs.play.services.maps)
}