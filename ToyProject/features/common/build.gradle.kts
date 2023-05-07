plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
}

@Suppress("UnstableApiUsage")
android {
    compileSdk = Versions.compileSdk

    defaultConfig {
        minSdk = Versions.minSdk
        targetSdk = Versions.targetSdk
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.compose.compiler.get()
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    namespace = "com.jooheon.clean_architecture.features.common"
}

dependencies {
    implementation(project(path = ":domain"))

    // android
    implementation(libs.androidx.core.ktx)

    // glide
    implementation(libs.bumptech.glide)
    annotationProcessor(libs.bumptech.glide.compiler)

    // timber
    implementation(libs.jakewharton.timber)

    // compose_material3
    implementation(libs.androidx.compose.material3)

    // accompanist
    implementation(libs.google.accompanist.systemuicontroller)

    // test
    testImplementation(libs.test.junit)
    androidTestImplementation(libs.test.android.junit)
    androidTestImplementation(libs.test.android.espresso.core)
}