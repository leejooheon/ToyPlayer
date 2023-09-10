plugins {
    id("org.jetbrains.kotlin.jvm")
    id("org.jetbrains.kotlin.plugin.serialization")
}

java {
    sourceCompatibility = App.Versions.javaCompileVersion
    targetCompatibility = App.Versions.javaCompileVersion
}

dependencies {
    implementation(libs.javax.inject)
    implementation(libs.kotlinx.serialization.json)

    implementation(libs.kotlinx.coroutines.core)
    testImplementation(libs.kotlinx.coroutines.test)
}