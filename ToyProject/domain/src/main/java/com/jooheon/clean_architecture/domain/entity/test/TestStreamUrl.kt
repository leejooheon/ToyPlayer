package com.jooheon.clean_architecture.domain.entity.test

import com.jooheon.clean_architecture.domain.entity.music.Song

class TestStreamUrl {
    companion object {
        val list = listOf(
            Song.default.copy(
                audioId = 8086,
                title = "Pop",
                artist = "Unknown artist (just streaming)",
                album = "Unknown album (just streaming)",
                path = "http://mediaserv30.live-streams.nl:{port}/live",
                imageUrl = "https://source.unsplash.com/pGM4sjt_BdQ",
            ),
            Song.default.copy(
                audioId = 8034,
                title = "Latin",
                artist = "Unknown artist (just streaming)",
                album = "Unknown album (just streaming)",
                path = "http://mediaserv30.live-streams.nl:{port}/live",
                imageUrl = "https://source.unsplash.com/Yc5sL-ejk6U",
            ),
            Song.default.copy(
                audioId = 8006,
                title = "Jazz",
                artist = "Unknown artist (just streaming)",
                album = "Unknown album (just streaming)",
                path = "http://mediaserv30.live-streams.nl:{port}/live",
                imageUrl = "https://source.unsplash.com/-LojFX9NfPY",
            ),
            Song.default.copy(
                audioId = 8036,
                title = "Lounge",
                artist = "Unknown artist (just streaming)",
                album = "Unknown album (just streaming)",
                path = "http://mediaserv30.live-streams.nl:{port}/live",
                imageUrl = "https://source.unsplash.com/3U2V5WqK1PQ",
            ),
            Song.default.copy(
                audioId = 8000,
                title = "Gold",
                artist = "Unknown artist (just streaming)",
                album = "Unknown album (just streaming)",
                path = "http://mediaserv30.live-streams.nl:{port}/live",
                imageUrl = "https://source.unsplash.com/Y4YR9OjdIMk",
            ),
            Song.default.copy(
                audioId = 8088,
                title = "Classical",
                artist = "Unknown artist (just streaming)",
                album = "Unknown album (just streaming)",
                path = "http://mediaserv30.live-streams.nl:{port}/live",
                imageUrl = "https://source.unsplash.com/Y4YR9OjdIMk",
            ),
            Song.default.copy(
                audioId = 8027,
                title = "World",
                artist = "Unknown artist (just streaming)",
                album = "Unknown album (just streaming)",
                path = "http://mediaserv30.live-streams.nl:{port}/live",
                imageUrl = "https://source.unsplash.com/YgYJsFDd4AU",
            ),
            Song.default.copy(
                audioId = 8000,
                title = "France",
                artist = "Unknown artist (just streaming)",
                album = "Unknown album (just streaming)",
                path = "http://mediaserv30.live-streams.nl:{port}/live",
                imageUrl = "https://source.unsplash.com/0u_vbeOkMpk",
            ),
        )
    }
}