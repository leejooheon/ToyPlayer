import com.jooheon.toyplayer.setNamespace

plugins {
    id("toyplayer.android.library")

    alias(libs.plugins.ksp)
    alias(libs.plugins.kotlin.serialization)
}

android {
    setNamespace("data.playlist")
}

dependencies {
    implementation(projects.domain)

    implementation(libs.javax.inject)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.jakewharton.serialization.converter)

    implementation(libs.androidx.room)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compile)
}