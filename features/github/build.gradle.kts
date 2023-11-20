plugins {
    id("toyproject.android.feature")
}

android {
    namespace = "com.jooheon.clean_architecture.toyproject.features.github"
}

dependencies {
    implementation(libs.kotlinx.serialization.json)
}