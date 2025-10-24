package com.jooheon.toyplayer.features.upnp

import com.jooheon.toyplayer.domain.model.common.extension.default
import com.jooheon.toyplayer.features.upnp.model.DlnaSpec
import com.jooheon.toyplayer.features.upnp.model.GenaEvent
import com.jooheon.toyplayer.features.upnp.protocol.instanceId
import com.jooheon.toyplayer.features.upnp.protocol.parseDurationToMillis
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import org.jupnp.android.AndroidUpnpService
import org.jupnp.controlpoint.SubscriptionCallback
import org.jupnp.model.gena.CancelReason
import org.jupnp.model.gena.GENASubscription
import org.jupnp.model.message.UpnpResponse
import org.jupnp.model.meta.RemoteDevice
import org.jupnp.support.avtransport.lastchange.AVTransportLastChangeParser
import org.jupnp.support.avtransport.lastchange.AVTransportVariable
import org.jupnp.support.lastchange.LastChange
import timber.log.Timber
import java.lang.Exception
import javax.inject.Inject

class DlnaConnectionSupervisor @Inject constructor() {
    fun observe(
        service: AndroidUpnpService,
        renderer: RemoteDevice,
    ): Flow<GenaEvent> {
        val requestedDurationSeconds = 600 // 10분
        val serviceType = renderer.findService(DlnaSpec.AVTransport.type)

        return callbackFlow {
            val callback = object : SubscriptionCallback(serviceType, requestedDurationSeconds) {
                override fun established(subscription: GENASubscription<*>?) {
                    Timber.d("established: ${subscription?.subscriptionId}")
                }

                override fun eventReceived(subscription: GENASubscription<*>?) {
                    Timber.d("eventReceived: $subscription")
                    val values = subscription?.currentValues
                    val xml = values?.get("LastChange")?.value as? String ?: return

                    val last = LastChange(AVTransportLastChangeParser(), xml)
                    val id = instanceId

                    launch {
                        last.getEventedValue(id, AVTransportVariable.TransportState::class.java)?.let {
                            val transportState = it.value
                            val event = GenaEvent.OnStateChanged(transportState.value)
                            Timber.d("send TransportState: $event")
                            send(event)
                        }
                        last.getEventedValue(id, AVTransportVariable.TransportStatus::class.java)?.let {
                            val transportStatus = it.value
                            val event = GenaEvent.OnStatusChanged(transportStatus.value)
                            Timber.d("send TransportStatus: $event")
                            send(event)
                        }
                        last.getEventedValue(id, AVTransportVariable.CurrentPlayMode::class.java)?.let {
                            val playMode = it.value
                            val event = GenaEvent.OnPlayModeChanged(playMode.name)
                            Timber.d("send CurrentPlayMode: $event")
                            send(event)
                        }
                        last.getEventedValue(id, AVTransportVariable.CurrentTrackDuration::class.java)?.let {
                            val realTimeTarget = it.value
                            val duration = parseDurationToMillis(realTimeTarget)
                            val event = GenaEvent.OnTrackDurationChanged(duration)
                            Timber.d("send CurrentTrackDuration: $event")
                            send(event)
                        }
                    }
                }

                override fun ended(
                    subscription: GENASubscription<*>?,
                    reason: CancelReason?,
                    responseStatus: UpnpResponse?
                ) {
                    Timber.w("ended: reason=$reason http=${responseStatus?.statusCode}")
                    launch {
                        when(reason) {
                            CancelReason.EXPIRED -> send(GenaEvent.OnSubscriptionExpired)
                            else -> send(GenaEvent.OnSubscriptionFailed)
                        }
                    }
                }

                override fun eventsMissed(
                    subscription: GENASubscription<*>?,
                    numberOfMissedEvents: Int
                ) {
                    Timber.w("eventsMissed: $numberOfMissedEvents")
                    launch {
                        send(GenaEvent.OnEventMissed) // 전체 상태를 GetTransportInfo 등으로 재동기화
                    }
                }

                override fun failed(
                    subscription: GENASubscription<*>?,
                    responseStatus: UpnpResponse?,
                    exception: Exception?,
                    defaultMsg: String?
                ) {
                    val message = responseStatus?.let { "status: ${it.statusMessage}, details: ${it.responseDetails}" }
                        .default(defaultMsg.default("UpnpResponse is null"))
                    Timber.e("failed: $message")

                    launch {
                        send(GenaEvent.OnSubscriptionFailed)
                    }
                }
            }

            service.controlPoint.execute(callback)
            awaitClose {
                callback.end()
            }
        }.distinctUntilChanged()
    }
}