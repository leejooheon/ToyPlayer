plugins {
    id("toyproject.android.feature")
}

android {
    namespace = App.Module.Features.nameSpace + ".github"
}

dependencies {
    implementation(libs.kotlinx.serialization.json)
}