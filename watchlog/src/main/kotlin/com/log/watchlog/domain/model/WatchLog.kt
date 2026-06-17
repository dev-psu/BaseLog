package com.log.watchlog.domain.model

import com.log.common.domain.KboTeam
import java.time.LocalDateTime

data class WatchLog(
    val id: Long = 0,
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
    val images: List<WatchLogImage>,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
)
