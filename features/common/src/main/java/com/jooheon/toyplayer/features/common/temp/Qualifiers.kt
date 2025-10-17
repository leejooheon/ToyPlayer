package com.jooheon.toyplayer.features.common.temp

import javax.inject.Qualifier


@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class MusicServiceContext

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class MusicServiceCoroutineScope