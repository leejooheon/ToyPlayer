import com.jooheon.toyplayer.setNamespace

plugins {
    id("toyplayer.android.feature")
}

android {
    setNamespace("features.upnp")
}

dependencies {
    implementation("org.jupnp.bom:org.jupnp.bom:3.0.3")

    implementation("org.eclipse.jetty:jetty-server:9.4.58.v20250814")
    implementation("org.eclipse.jetty:jetty-servlet:9.4.58.v20250814")
    implementation("org.eclipse.jetty:jetty-client:9.4.58.v20250814")
    implementation("org.slf4j:slf4j-jdk14:2.0.17") // 1.7.36

    implementation("org.nanohttpd:nanohttpd:2.3.1")
}