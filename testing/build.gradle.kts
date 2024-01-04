plugins {
    id("toyplayer.android.library")
}

android {
    namespace = "com.jooheon.testing"
}

dependencies {
    api(libs.junit4)
    api(libs.junit.vintage.engine)
    api(libs.kotlin.test)
    api(libs.mockk)
    api(libs.turbine)
    api(libs.kotlinx.coroutines.test)
}