package com.log.watchlog.domain.port.input

import com.log.common.domain.KboTeam
import com.log.watchlog.domain.model.*

interface UpdateWatchLogUseCase {
    fun update(command: UpdateWatchLogCommand): WatchLog
}

data class UpdateWatchLogCommand(
    val watchLogId: Long,
    val requesterId: Long,
    val watchType: WatchType,
    val cheeringTeam: KboTeam,
    val result: WatchResult,
    val title: String?,
    val content: String?,
    val seatInfo: SeatInfo?,
    val companions: String?,
    val mood: Mood?,
    val cost: WatchCost?,
    val weather: WeatherInfo?,
    val isPublic: Boolean,
    val images: List<ImageCommand>,
)
