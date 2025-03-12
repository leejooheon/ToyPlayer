package com.jooheon.toyplayer.data.music.etc

import com.jooheon.toyplayer.domain.model.RadioRawData
import com.jooheon.toyplayer.domain.model.music.Song

val kbsStations = listOf(
    RadioRawData(
        channelName = "KBS 1라디오",
        channelCode = 21.toString(),
    ),
    RadioRawData(
        channelName = "KBS 2라디오",
        channelCode = 22.toString(),
    ),
//    RadioRawData(
//        channelName = "KBS 3라디오",
//        channelCode = 23.toString(),
//    ),
//    RadioRawData(
//        channelName = "KBS 1FM",
//        channelCode = 24.toString(),
//    ),
//    RadioRawData(
//        channelName = "KBS 2FM",
//        channelCode = 25.toString(),
//    ),
//    RadioRawData(
//        channelName = "KBS 한민족 방송",
//        channelCode = 26.toString(),
//    ),
)

val mbcStations = listOf(
    RadioRawData(
        channelName = "MBC 표준FM",
        channelCode = "sfm",
    ),
    RadioRawData(
        channelName = "MBC FM4U",
        channelCode = "mfm",
    ),
    RadioRawData(
        channelName = "MBC 올댓뮤직",
        channelCode = "chm",
    ),
)

val sbsStations = listOf(
    RadioRawData(
        channelName = "SBS 파워FM",
        channelCode = "powerpc",
        channelSubCode = "powerfm"
    ),
    RadioRawData(
        channelName = "SBS 러브FM",
        channelCode = "lovepc",
        channelSubCode = "lovefm"
    ),
    RadioRawData(
        channelName = "SBS 고릴라디오M",
        channelCode = "sbsdmbpc",
        channelSubCode = "sbsdmb"
    ),
)

val radioStations = listOf(
    Song(
        audioId = "TBN 경인교통방송".hashCode().toLong(),
        useCache = false,
        displayName = "TBN 경인교통방송",
        title = "TBN 경인교통방송",
        artist = "TBN",
        artistId = "",
        album = "Live Radio",
        albumId = "",
        duration = -1,
        path = "http://radio2.tbn.or.kr:1935/gyeongin/myStream/playlist.m3u8",
        trackNumber = 0,
        imageUrl = "",
        isFavorite = false,
        data = null
    ),
    Song(
        audioId = "CBS 표준FM".hashCode().toLong(),
        useCache = false,
        displayName = "CBS 표준FM",
        title = "CBS 표준FM",
        artist = "CBS",
        artistId = "",
        album = "Live Radio",
        albumId = "",
        duration = -1,
        path = "https://aac.cbs.co.kr/cbs981/_definst_/cbs981.stream/playlist.m3u8",
        trackNumber = 0,
        imageUrl = "",
        isFavorite = false,
        data = null
    ),
    Song(
        audioId = "CBS 음악FM".hashCode().toLong(),
        useCache = false,
        displayName = "CBS 음악FM",
        title = "CBS 음악FM",
        artist = "CBS",
        artistId = "",
        album = "Live Radio",
        albumId = "",
        duration = -1,
        path = "https://aac.cbs.co.kr/cbs981/_definst_/cbs981.stream/playlist.m3u8",
        trackNumber = 0,
        imageUrl = "",
        isFavorite = false,
        data = null
    ),
    Song(
        audioId = "FEBC 서울극동방송".hashCode().toLong(),
        useCache = false,
        displayName = "FEBC 서울극동방송",
        title = "FEBC 서울극동방송",
        artist = "FEBC",
        artistId = "",
        album = "Live Radio",
        albumId = "",
        duration = -1,
        path = "http://mlive2.febc.net:1935/live/seoulfm/playlist.m3u8",
        trackNumber = 0,
        imageUrl = "",
        isFavorite = false,
        data = null
    ),
    Song(
        audioId = "BBS 서울불교방송".hashCode().toLong(),
        useCache = false,
        displayName = "BBS 서울불교방송",
        title = "BBS 서울불교방송",
        artist = "BBS",
        artistId = "",
        album = "Live Radio",
        albumId = "",
        duration = -1,
        path = "https://bbslive.clouducs.com/bbsradio-live/livestream/playlist.m3u8",
        trackNumber = 0,
        imageUrl = "",
        isFavorite = false,
        data = null
    ),
    Song(
        audioId = "EBS FM".hashCode().toLong(),
        useCache = false,
        displayName = "EBS FM",
        title = "EBS FM",
        artist = "EBS",
        artistId = "",
        album = "Live Radio",
        albumId = "",
        duration = -1,
        path = "https://ebsonair.ebs.co.kr/fmradiofamilypc/familypc1m/playlist.m3u8",
        trackNumber = 0,
        imageUrl = "",
        isFavorite = false,
        data = null
    ),
    Song(
        audioId = "YTN 라디오".hashCode().toLong(),
        useCache = false,
        displayName = "YTN 라디오",
        title = "YTN 라디오",
        artist = "YTN",
        artistId = "",
        album = "Live Radio",
        albumId = "",
        duration = -1,
        path = "https://radiolive.ytn.co.kr/radio/_definst_/20211118_fmlive/playlist.m3u8",
        trackNumber = 0,
        imageUrl = "",
        isFavorite = false,
        data = null
    ),
    Song(
        audioId = "국악방송".hashCode().toLong(),
        useCache = false,
        displayName = "국악방송",
        title = "국악방송",
        artist = "국악방송",
        artistId = "",
        album = "Live Radio",
        albumId = "",
        duration = -1,
        path = "https://mgugaklive.nowcdn.co.kr/gugakradio/gugakradio.stream/playlist.m3u8",
        trackNumber = 0,
        imageUrl = "",
        isFavorite = false,
        data = null
    )
)
