import com.jooheon.toyplayer.setNamespace

plugins {
    id("toyplayer.android.library")
}

android {
    setNamespace("data.system")
}

dependencies {
    implementation(projects.domain)

    implementation(libs.javax.inject)
}