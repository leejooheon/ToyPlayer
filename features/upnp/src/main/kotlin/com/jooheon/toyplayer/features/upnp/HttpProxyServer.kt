package com.jooheon.toyplayer.features.upnp

import fi.iki.elonen.NanoHTTPD
import java.io.ByteArrayInputStream
import java.net.HttpURLConnection
import java.net.URL
import kotlin.collections.firstOrNull
import kotlin.text.isNullOrBlank

class HttpProxyServer() : NanoHTTPD(PORT) {
    companion object {
        internal const val PORT = 18080
    }

    override fun serve(s: IHTTPSession): Response {
        val src = s.parameters["u"]?.firstOrNull()
            ?: return newFixedLengthResponse(Response.Status.BAD_REQUEST, "text/plain", "missing u")

        val reqRange = s.headers["range"] // ex) bytes=12345-
        val method = if (s.method == Method.HEAD) "HEAD" else "GET"

        val conn = (URL(src).openConnection() as HttpURLConnection).apply {
            instanceFollowRedirects = true
            requestMethod = method
            if (!reqRange.isNullOrBlank()) setRequestProperty("Range", reqRange)
            connect()
        }

        val code = conn.responseCode
        val status = if (code == 206) Response.Status.PARTIAL_CONTENT else Response.Status.OK
        val mime = "audio/mpeg"//(conn.contentType ?: "audio/mpeg").substringBefore(';')
        val len  = conn.getHeaderFieldLong("Content-Length", -1L)
        val cr   = conn.getHeaderField("Content-Range")

        // HEAD → 바디 없이 헤더만
        if (method == "HEAD") {
            return newFixedLengthResponse(status, mime, ByteArrayInputStream(ByteArray(0)), 0).apply {
                addHeader("Accept-Ranges", "bytes")
                if (len >= 0) addHeader("Content-Length", "$len")
                if (cr != null) addHeader("Content-Range", cr)
                addHeader("Connection", "keep-alive")
            }
        }

        return (if (len > 0)
            newFixedLengthResponse(status, mime, conn.inputStream, len)
        else
            newChunkedResponse(status, mime, conn.inputStream)
                ).apply {
                addHeader("Accept-Ranges", "bytes")
                if (cr != null) addHeader("Content-Range", cr)
                addHeader("Connection", "keep-alive")
            }
    }
}