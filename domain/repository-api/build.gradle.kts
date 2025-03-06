plugins {
    id("org.jetbrains.kotlin.jvm")
    id("org.jetbrains.kotlin.plugin.serialization")
}

kotlin {
    jvmToolchain(17)
}

dependencies {
    implementation(projects.domain.model)

    implementation(libs.javax.inject)
    implementation(libs.kotlinx.coroutines.core)
}