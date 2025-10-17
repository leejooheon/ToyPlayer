package com.jooheon.toyplayer.features.upnp.model

import org.jupnp.model.types.UDAServiceType

sealed interface DlnaSpec {
    val type: UDAServiceType

    data object ConnectionManager : DlnaSpec { override val type =
        UDAServiceType("ConnectionManager")
    }
    data object AVTransport : DlnaSpec { override val type = UDAServiceType("AVTransport") }
    data object RenderingControl : DlnaSpec { override val type =
        UDAServiceType("RenderingControl")
    }
}