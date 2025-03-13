package com.jooheon.toyplayer.data.music.etc

import com.jooheon.toyplayer.domain.model.radio.RadioData
import com.jooheon.toyplayer.domain.model.music.Song
import com.jooheon.toyplayer.domain.model.radio.RadioType

val kbsStations = listOf(
    RadioData(
        type = RadioType.Kbs,
        url = null,
        channelName = "KBS 1라디오",
        channelCode = 21.toString(),
    ),
    RadioData(
        type = RadioType.Kbs,
        url = null,
        channelName = "KBS 2라디오",
        channelCode = 22.toString(),
    ),
    RadioData(
        type = RadioType.Kbs,
        url = null,
        channelName = "KBS 3라디오",
        channelCode = 23.toString(),
    ),
    RadioData(
        type = RadioType.Kbs,
        url = null,
        channelName = "KBS 1FM",
        channelCode = 24.toString(),
    ),
    RadioData(
        type = RadioType.Kbs,
        url = null,
        channelName = "KBS 2FM",
        channelCode = 25.toString(),
    ),
    RadioData(
        type = RadioType.Kbs,
        url = null,
        channelName = "KBS 한민족 방송",
        channelCode = 26.toString(),
    ),
)

val mbcStations = listOf(
    RadioData(
        type = RadioType.Mbc,
        url = null,
        channelName = "MBC 표준FM",
        channelCode = "sfm",
    ),
    RadioData(
        type = RadioType.Mbc,
        url = null,
        channelName = "MBC FM4U",
        channelCode = "mfm",
    ),
    RadioData(
        type = RadioType.Mbc,
        url = null,
        channelName = "MBC 올댓뮤직",
        channelCode = "chm",
    ),
)

val sbsStations = listOf(
    RadioData(
        type = RadioType.Sbs,
        url = null,
        channelName = "SBS 파워FM",
        channelCode = "powerpc",
        channelSubCode = "powerfm"
    ),
    RadioData(
        type = RadioType.Sbs,
        url = null,
        channelName = "SBS 러브FM",
        channelCode = "lovepc",
        channelSubCode = "lovefm"
    ),
    RadioData(
        type = RadioType.Sbs,
        url = null,
        channelName = "SBS 고릴라디오M",
        channelCode = "sbsdmbpc",
        channelSubCode = "sbsdmb"
    ),
)

val etcStations = listOf(
    RadioData(
        type = RadioType.Etc("TBN"),
        url = "http://radio2.tbn.or.kr:1935/gyeongin/myStream/playlist.m3u8",
        channelName = "TBN 경인교통방송",
        channelCode = "TBN 경인교통방송"
    ),
    RadioData(
        type = RadioType.Etc("CBS"),
        url = "https://aac.cbs.co.kr/cbs981/_definst_/cbs981.stream/playlist.m3u8",
        channelName = "CBS 표준FM",
        channelCode = "CBS 표준FM"
    ),
    RadioData(
        type = RadioType.Etc("CBS"),
        url = "https://aac.cbs.co.kr/cbs981/_definst_/cbs981.stream/playlist.m3u8",
        channelName = "CBS 음악FM",
        channelCode = "CBS 음악FM"
    ),
    RadioData(
        type = RadioType.Etc("FEBC"),
        url = "http://mlive2.febc.net:1935/live/seoulfm/playlist.m3u8",
        channelName = "FEBC 서울극동방송",
        channelCode = "FEBC 서울극동방송"
    ),
    RadioData(
        type = RadioType.Etc("BBS"),
        url = "https://bbslive.clouducs.com/bbsradio-live/livestream/playlist.m3u8",
        channelName = "BBS 서울불교방송",
        channelCode = "BBS 서울불교방송"
    ),
    RadioData(
        type = RadioType.Etc("EBS"),
        url = "https://ebsonair.ebs.co.kr/fmradiofamilypc/familypc1m/playlist.m3u8",
        channelName = "EBS FM",
        channelCode = "EBS FM"
    ),
    RadioData(
        type = RadioType.Etc("YTN"),
        url = "https://radiolive.ytn.co.kr/radio/_definst_/20211118_fmlive/playlist.m3u8",
        channelName = "YTN 라디오",
        channelCode = "YTN 라디오"
    ),
    RadioData(
        type = RadioType.Etc("LGBF"),
        url = "https://mgugaklive.nowcdn.co.kr/gugakradio/gugakradio.stream/playlist.m3u8",
        channelName = "국악방송",
        channelCode = "국악방송"
    )
)