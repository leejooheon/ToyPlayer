import com.jooheon.toyplayer.setNamespace

plugins {
    id("toyplayer.android.library")
    id("toyplayer.android.hilt")
}

android {
    setNamespace("data.datastore")
}

dependencies {
    implementation(projects.domain.model)
    implementation(libs.androidx.datastore.preference)
}