package com.jooheon.toyplayer.data.datastore.di

import javax.inject.Qualifier

object DataStoreQualifier {
    @Qualifier
    @Retention(AnnotationRetention.RUNTIME)
    annotation class Default

    @Qualifier
    @Retention(AnnotationRetention.RUNTIME)
    annotation class Playback
}