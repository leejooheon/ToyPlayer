package com.jooheon.toyplayer.features.common.permission

import android.Manifest
import com.jooheon.toyplayer.features.common.utils.VersionUtil


val audioStoragePermission = if (VersionUtil.hasTiramisu()) {
    Manifest.permission.READ_MEDIA_AUDIO
} else {
    Manifest.permission.READ_EXTERNAL_STORAGE
}