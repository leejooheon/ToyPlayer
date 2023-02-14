package com.jooheon.clean_architecture.presentation.widget

import android.content.Context
import android.util.Log
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.hilt.work.HiltWorker
import androidx.work.*
import com.jooheon.clean_architecture.domain.common.Resource
import com.jooheon.clean_architecture.domain.entity.Entity
import com.jooheon.clean_architecture.domain.usecase.subway.SubwayUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import java.util.*

@HiltWorker
class SubwayWidgetUpdateWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted workerParameters: WorkerParameters,
    private val subwayUseCase: SubwayUseCase,
): CoroutineWorker(context, workerParameters) {

    override suspend fun doWork(): Result {
        Log.d(TAG, "doWork - start")
        val manager = GlanceAppWidgetManager(context)
        val glanceIds = manager.getGlanceIds(SubwayWidget::class.java)

        val resources = withContext(Dispatchers.IO) {
            Log.d(TAG, "doWork: ${glanceIds.first()}")
            val statioNames = getStationNames()
            val firstStationInfo = async { subwayUseCase.getStationInfoSync(statioNames.first) }.await()
            val secondStationInfo = async { subwayUseCase.getStationInfoSync(statioNames.second) }.await()
            listOf(firstStationInfo, secondStationInfo)
        }

        val subwayInfoList = resources
            .filterIsInstance<Resource.Success<Entity.Station>>()
            .map { parseData(it.value) }

        Log.d(TAG, "subwayInfoList size: ${subwayInfoList.size}")
        subwayInfoList.forEach {
            Log.d(TAG, "upLineTrain: ${it.upLineTrain}")
            Log.d(TAG, "downLineTrain: ${it.downLineTrain}")
            Log.d(TAG, "============================================================")
        }

        if(subwayInfoList.size != 2) {
            return Result.failure()
        }

        updateAppWidgetState(
            context = context,
            definition = SubwayInfoDefinition,
            glanceId = glanceIds.first(),
            updateState = { subwayInfoList }
        )

        return Result.success()
    }

    private fun isMorning(): Boolean {
        val hour24 = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        return hour24 <= 12
    }

    private fun parseData(station: Entity.Station): SubWayInfo {
        if(station.realtimeArrivalList.isEmpty())
            return SubWayInfo.TEST_ONE

        val data = station.realtimeArrivalList.first()
        val upLine = station.realtimeArrivalList.filter {
            it.updnLine == UP_LINE
        }.first()
        val downLine = station.realtimeArrivalList.filter {
            it.updnLine == DOWN_LINE
        }.first()

        val subWayInfo = SubWayInfo(
            stationName = data.statnNm ?: "",
            upLineTrain = upLine.trainLineNm ?: "",
            downLineTrain = downLine.trainLineNm ?: "",
            upLineTime = listOf(upLine.arvlMsg2 ?: "", upLine.arvlMsg3 ?: ""),
            downLineTime = listOf(downLine.arvlMsg2 ?: "", downLine.arvlMsg3 ?: "")
        )

        return subWayInfo
    }

    private fun getStationNames(): Pair<String, String> {
        if(isMorning()) {
            return Pair("염창", "논현") // 출근 시간
        } else {
            return Pair("판교", "논현") // 퇴근  시간
        }
    }

    companion object {
        private val TAG = SubwayWidgetUpdateWorker::class.java.simpleName
        const val UP_LINE = "상행"
        const val DOWN_LINE = "하행"
        fun enqueue(context: Context) {
            val requestBuilder = OneTimeWorkRequestBuilder<SubwayWidgetUpdateWorker>()

            WorkManager.getInstance(context).enqueueUniqueWork(
                TAG,
                ExistingWorkPolicy.KEEP,
                requestBuilder.build()
            )
        }

        fun cancel(context: Context) {
            WorkManager.getInstance(context).cancelUniqueWork(TAG)
        }
    }
}