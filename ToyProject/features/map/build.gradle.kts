plugins {
    id("toyproject.android.feature")
}

android {
    namespace = "com.jooheon.clean_architecture.toyproject.features.map"
}

dependencies {
    // google map
    implementation("com.google.maps.android:maps-compose:2.5.3")
    implementation("com.google.android.gms:play-services-maps:18.1.0")
}