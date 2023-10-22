import com.jooheon.toyproject.configureKotlinAndroid
import com.jooheon.toyproject.configureHiltAndroid
import com.jooheon.toyproject.configureHiltWorkAndroid
import com.jooheon.toyproject.configureKotestAndroid

plugins {
    id("com.android.application")
}

configureKotlinAndroid()
configureHiltAndroid()
configureHiltWorkAndroid()
configureKotestAndroid()
