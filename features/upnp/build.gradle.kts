import com.jooheon.toyplayer.setNamespace

plugins {
    id("toyplayer.android.feature")
}

android {
    setNamespace("features.upnp")
}

dependencies {
    implementation(libs.org.jupnp.bom)

    implementation(libs.jetty.server)
    implementation(libs.jetty.servlet)
    implementation(libs.jetty.client)
    implementation(libs.slf4j)

    implementation(libs.nanohttpd)
}