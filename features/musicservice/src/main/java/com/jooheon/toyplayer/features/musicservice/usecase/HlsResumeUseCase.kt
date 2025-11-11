package com.jooheon.toyplayer.features.musicservice.usecase

import androidx.media3.common.Player
import com.jooheon.toyplayer.features.musicservice.MusicStateHolder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import timber.log.Timber

class HlsResumeUseCase(
    private val musicStateHolder: MusicStateHolder,
) {
    private val _resume = Channel<Unit>()
    internal val resume = _resume.receiveAsFlow()

    @Volatile
    private var latestSuppressionReason = Player.PLAYBACK_SUPPRESSION_REASON_NONE

    internal fun initialize(scope: CoroutineScope) = scope.launch{
        combine(
            musicStateHolder.isPlaying,
            musicStateHolder.suppressionReason
        ) { isPlaying, suppressionReason ->
            isPlaying to suppressionReason
        }.collectLatest { (isPlaying, suppressionReason) ->
            Timber.d("isPlaying: $isPlaying, reason: ${suppressionReason.reasonString()}")
            when(suppressionReason) {
                Player.PLAYBACK_SUPPRESSION_REASON_NONE -> {
                    if(isPlaying && latestSuppressionReason == Player.PLAYBACK_SUPPRESSION_REASON_TRANSIENT_AUDIO_FOCUS_LOSS) {
                        _resume.send(Unit)
                    }
                }
            }
            latestSuppressionReason = suppressionReason
        }
    }

    private fun Int.reasonString(): String {
        return when (this) {
            Player.PLAYBACK_SUPPRESSION_REASON_NONE -> "PLAYBACK_SUPPRESSION_REASON_NONE"
            Player.PLAYBACK_SUPPRESSION_REASON_TRANSIENT_AUDIO_FOCUS_LOSS -> "PLAYBACK_SUPPRESSION_REASON_TRANSIENT_AUDIO_FOCUS_LOSS"
            Player.PLAYBACK_SUPPRESSION_REASON_UNSUITABLE_AUDIO_OUTPUT -> "PLAYBACK_SUPPRESSION_REASON_UNSUITABLE_AUDIO_OUTPUT"
            Player.PLAYBACK_SUPPRESSION_REASON_SCRUBBING -> "PLAYBACK_SUPPRESSION_REASON_SCRUBBING"
            else -> throw IllegalArgumentException("Unknown reason: $this")
        }
    }

//    when (reason) {
//        Player.PLAYBACK_SUPPRESSION_REASON_NONE -> 재생 가능 상태
//        Player.PLAYBACK_SUPPRESSION_REASON_TRANSIENT_AUDIO_FOCUS_LOSS -> 포커스 일시 손실 (카톡/네비/TTS 등)
//        Player.PLAYBACK_SUPPRESSION_REASON_UNSUITABLE_AUDIO_OUTPUT -> 오디오 출력 경로 전환 중 → 기다리는 중
//        Player.PLAYBACK_SUPPRESSION_REASON_SCRUBBING -> 사용자 시킹 중 → 일시적 정지
//    }
}