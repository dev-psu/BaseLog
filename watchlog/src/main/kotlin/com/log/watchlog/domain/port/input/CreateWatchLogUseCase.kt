package com.log.watchlog.domain.port.input

import com.log.common.domain.KboTeam
import com.log.watchlog.domain.model.*

interface CreateWatchLogUseCase {
    fun create(command: CreateWatchLogCommand): WatchLog
}

data class CreateWatchLogCommand(
    val memberId: Long,
    val gameId: Long?,
    val gameSeason: Int?,
    val venue: String?,
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

data class ImageCommand(
    val imageUrl: String,
    val imageType: ImageType,
    val sortOrder: Int,
)
