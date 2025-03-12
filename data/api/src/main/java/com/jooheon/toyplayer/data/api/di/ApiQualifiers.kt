package com.jooheon.toyplayer.data.api.di

import javax.inject.Qualifier


object RetrofitQualifier {
    @Qualifier
    @Retention(AnnotationRetention.RUNTIME)
    annotation class KbsServer

    @Qualifier
    @Retention(AnnotationRetention.RUNTIME)
    annotation class MbcServer

    @Qualifier
    @Retention(AnnotationRetention.RUNTIME)
    annotation class SbsServer
}

object ConverterQualifier {
    @Qualifier
    @Retention(AnnotationRetention.RUNTIME)
    annotation class Json

    @Qualifier
    @Retention(AnnotationRetention.RUNTIME)
    annotation class Scalars
}