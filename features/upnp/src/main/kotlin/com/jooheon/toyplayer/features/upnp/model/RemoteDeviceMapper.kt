package com.jooheon.toyplayer.features.upnp.model

import com.jooheon.toyplayer.domain.model.cast.DlnaRendererModel
import com.jooheon.toyplayer.domain.model.common.extension.defaultEmpty
import org.jupnp.model.meta.RemoteDevice

fun RemoteDevice.toModel() = DlnaRendererModel(
    name = details.friendlyName.defaultEmpty(),
    udn = identity.udn.identifierString.defaultEmpty(),
)