package com.jooheon.clean_architecture.features.widget

import kotlinx.serialization.Serializable

@Serializable
internal data class SubWayInfo(
    val stationName: String,
    val upLineTrain: String,
    val downLineTrain: String,
    val upLineTime: List<String>,
    val downLineTime: List<String>
) {
    companion object {
        val TEST_ONE = SubWayInfo(
            stationName = "판교",
            upLineTrain = "신사행 - 청계산입구방면",
            downLineTrain = "광교행 - 정자방면",
            upLineTime = listOf("00:00", "11:11"),
            downLineTime = listOf("99:99", "88, 88")
        )

        val TEST_TWO = SubWayInfo(
            stationName = "염창",
            upLineTrain = "개화행 - 등촌방면",
            downLineTrain = "중앙보훈병원행 - 신목동방면",
            upLineTime = listOf("00:00", "11:11"),
            downLineTime = listOf("99:99", "88:88")
        )
    }
}
