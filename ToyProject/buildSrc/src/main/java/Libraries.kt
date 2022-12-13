object Libraries { // 각각의 모듈에서 중복된 라이브러리 항목만 배치합니다.
    // hilt
    const val hilt_version = "2.43.2"
    const val hilt = "com.google.dagger:hilt-android:$hilt_version"
    const val hiltDaggerCompiler = "com.google.dagger:hilt-android-compiler:$hilt_version"
    const val hiltForCompose = "androidx.hilt:hilt-navigation-compose:1.0.0"  // option: hilt <-> compose

    // coroutine
    const val coroutine_version = "1.6.4"
    const val coroutineCore = "org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutine_version"
    const val coroutineAndroid = "org.jetbrains.kotlinx:kotlinx-coroutines-android:$coroutine_version"

    // retrofit
    const val retrofit_version = "2.9.0"
    const val retrofit = "com.squareup.retrofit2:retrofit:$retrofit_version"
    const val retrofitConverter = "com.squareup.retrofit2:converter-gson:$retrofit_version"
    const val retrofitMock = "com.squareup.retrofit2:retrofit-mock:2.4.0"
    const val interceptor = "com.squareup.okhttp3:logging-interceptor:5.0.0-alpha.10"
    const val chuckLogging = "com.readystatesoftware.chuck:library:1.1.0"

    // xml component to rx observable
    const val rxConverter = "com.jakewharton.rxbinding4:rxbinding:4.0.0"  // xml componenet의 property들을 observable로 바꿔주는 라이브러리

    // room
    const val room_version = "2.4.3"
    const val room = "androidx.room:room-runtime:$room_version" // implementation
    const val roomCompile = "androidx.room:room-compiler:$room_version" // kapt
    const val room_ktx = "androidx.room:room-ktx:$room_version" // Kotlin Extensions and Coroutines support for Room
}
