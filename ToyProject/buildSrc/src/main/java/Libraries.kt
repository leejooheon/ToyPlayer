object Libraries { // 각각의 모듈에서 중복된 라이브러리 항목만 배치합니다.
    // hilt
    const val hilt = "com.google.dagger:hilt-android:2.40.5"
    const val hiltForCompose = "androidx.hilt:hilt-navigation-compose:1.0.0-rc01"  // option: hilt <-> compose
    const val hiltDaggerCompiler = "com.google.dagger:hilt-android-compiler:2.40.5"

    // coroutine
    const val coroutineCore = "org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.1"
    const val coroutineAndroid = "org.jetbrains.kotlinx:kotlinx-coroutines-android:1.6.1"

    // retrofit
    const val retrofit = "com.squareup.retrofit2:retrofit:2.9.0"
    const val retrofitConverter = "com.squareup.retrofit2:converter-gson:2.9.0"
    const val retrofitMock = "com.squareup.retrofit2:retrofit-mock:2.4.0"
    const val interceptor = "com.squareup.okhttp3:logging-interceptor:5.0.0-alpha.2"
    const val chuckLogging = "com.readystatesoftware.chuck:library:1.1.0"

    // xml component to rx observable
    const val rxConverter = "com.jakewharton.rxbinding4:rxbinding:4.0.0"  // xml componenet의 property들을 observable로 바꿔주는 라이브러리
}
