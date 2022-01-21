object Libraries {
    const val desugar = "com.android.tools:desugar_jdk_libs:1.1.5"
    const val multidex = "androidx.multidex:multidex:2.0.1"

    // hilt
    const val hilt = "com.google.dagger:hilt-android:2.40.5"
    const val hiltDaggerCompiler = "com.google.dagger:hilt-android-compiler:2.40.5"
    const val hiltForCompose = "androidx.hilt:hilt-navigation-compose:1.0.0-rc01"  // option: hilt <-> compose

    // coroutine
    const val coroutineCore = "org.jetbrains.kotlinx:kotlinx-coroutines-core:1.5.2"
    const val coroutineAndroid = "org.jetbrains.kotlinx:kotlinx-coroutines-android:1.5.2"

    // retrofit
    const val retrofit = "com.squareup.retrofit2:retrofit:2.9.0"
    const val retrofitConverter = "com.squareup.retrofit2:converter-gson:2.9.0"
    const val retrofitMock = "com.squareup.retrofit2:retrofit-mock:2.4.0"
    const val interceptor = "com.squareup.okhttp3:logging-interceptor:5.0.0-alpha.2"
    const val chuckLogging = "com.readystatesoftware.chuck:library:1.1.0"

    // xml component to rx observable
    const val rxConverter = "com.jakewharton.rxbinding4:rxbinding:4.0.0"  // xml componenet의 property들을 observable로 바꿔주는 라이브러리

    // fcm
    const val fcmPlatform = "com.google.firebase:firebase-bom:29.0.3"
    const val fcmMessaging = "com.google.firebase:firebase-messaging-ktx:23.0.0"

    // UI
    const val fragment = "androidx.fragment:fragment-ktx:1.4.0"
    const val navigationUI = "androidx.navigation:navigation-runtime-ktx:2.3.5"
    const val constraintLayout = "androidx.constraintlayout:constraintlayout:2.1.2"
    const val material = "com.google.android.material:material:1.4.0"
    const val splashScreen = "androidx.core:core-splashscreen:1.0.0-beta01"

    // Compose
    const val composeActivity = "androidx.activity:activity-compose:1.4.0"  // Integration with activities
    const val composeMaterial = "androidx.compose.material:material:1.0.5" // Compose Material Design
    const val composeAnimation = "androidx.compose.animation:animation:1.0.5" // Animations
    const val composeUiTools = "androidx.compose.ui:ui-tooling:1.0.5" // Tooling support (Previews, etc.)
    const val composeViewModel = "androidx.lifecycle:lifecycle-viewmodel-compose:2.4.0" // Integration with ViewModels
    const val composeIcon = "androidx.compose.material:material-icons-extended:1.2.0-alpha01"

    const val composeTest = "androidx.compose.ui:ui-test-junit4:1.0.5" // // UI Tests

    // Accompanist
    const val accompanistAnimation = "com.google.accompanist:accompanist-navigation-animation:0.21.5-rc"
    const val accompanistInsets = "com.google.accompanist:accompanist-insets:0.21.5-rc"
    const val accompanistInsetsUi = "com.google.accompanist:accompanist-insets-ui:0.21.5-rc"

    // lifecycle
    const val lifecycle = "androidx.lifecycle:lifecycle-runtime-ktx:2.4.0"
    const val lifecycleReactive = "androidx.lifecycle:lifecycle-reactivestreams-ktx:2.4.0"

    // support
    const val appCompat = "androidx.appcompat:appcompat:1.4.0"
    const val coreKtx = "androidx.core:core-ktx:1.7.0"

    // kotlin
    const val kotlin = "org.jetbrains.kotlin:kotlin-stdlib:"
}
