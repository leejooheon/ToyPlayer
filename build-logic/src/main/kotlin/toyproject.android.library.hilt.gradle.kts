import com.jooheon.toyproject.configureCoroutineAndroid
import com.jooheon.toyproject.configureHiltAndroid
import com.jooheon.toyproject.configureKotest
import com.jooheon.toyproject.configureKotlinAndroid

plugins {
    id("toyproject.android.library")
}

configureHiltAndroid()
