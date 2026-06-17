package com.log.watchlog.adapter.input.web

import com.log.common.domain.KboTeam
import com.log.watchlog.domain.model.*
import java.time.LocalDateTime

data class CreateWatchLogRequest(
    val gameId: Long?,
    val gameSeason: Int?,
    val venue: String?,
    val watchType: WatchType,
    val cheeringTeam: KboTeam,
    val result: WatchResult,
    val title: String?,
    val content: String?,
    val seatInfo: SeatInfoRequest?,
    val companions: String?,
    val mood: Mood?,
    val cost: WatchCostRequest?,
    val weather: WeatherInfoRequest?,
    val isPublic: Boolean = false,
    val images: List<ImageRequest> = emptyList(),
)

data class UpdateWatchLogRequest(
    val watchType: WatchType,
    val cheeringTeam: KboTeam,
    val result: WatchResult,
    val title: String?,
    val content: String?,
    val seatInfo: SeatInfoRequest?,
    val companions: String?,
    val mood: Mood?,
    val cost: WatchCostRequest?,
    val weather: WeatherInfoRequest?,
    val isPublic: Boolean,
    val images: List<ImageRequest> = emptyList(),
)

data class SeatInfoRequest(
    val grade: String?,
    val section: String?,
    val row: String?,
    val number: String?,
) {
    fun toDomain() = SeatInfo(grade = grade, section = section, row = row, number = number)
}

data class WatchCostRequest(
    val ticketCost: Int?,
    val foodCost: Int?,
    val transportCost: Int?,
) {
    fun toDomain() = WatchCost(ticketCost = ticketCost, foodCost = foodCost, transportCost = transportCost)
}

data class WeatherInfoRequest(
    val condition: WeatherCondition,
    val temperatureCelsius: Double?,
) {
    fun toDomain() = WeatherInfo(condition = condition, temperatureCelsius = temperatureCelsius)
}

data class ImageRequest(
    val imageUrl: String,
    val imageType: ImageType,
    val sortOrder: Int,
)

data class WatchLogResponse(
    val id: Long,
    val memberId: Long,
    val gameId: Long?,
    val gameSeason: Int?,
    val venue: String?,
    val watchType: WatchType,
    val cheeringTeam: KboTeam,
    val result: WatchResult,
    val title: String?,
    val content: String?,
    val seatInfo: SeatInfoResponse?,
    val companions: String?,
    val mood: Mood?,
    val cost: WatchCostResponse?,
    val weather: WeatherInfoResponse?,
    val isPublic: Boolean,
    val images: List<WatchLogImageResponse>,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
)

data class SeatInfoResponse(val grade: String?, val section: String?, val row: String?, val number: String?)
data class WatchCostResponse(val ticketCost: Int?, val foodCost: Int?, val transportCost: Int?, val total: Int)
data class WeatherInfoResponse(val condition: WeatherCondition, val temperatureCelsius: Double?)
data class WatchLogImageResponse(val id: Long, val imageUrl: String, val imageType: ImageType, val sortOrder: Int)

data class WatchStatsResponse(
    val totalCount: Int,
    val stadiumCount: Int,
    val homeCount: Int,
    val winCount: Int,
    val loseCount: Int,
    val drawCount: Int,
    val winRate: Double,
    val stadiumWinRate: Double,
    val homeWinRate: Double,
    val currentStreak: StreakResponse,
    val bestWinStreak: Int,
    val visitedVenues: List<String>,
    val seasonTotalCost: Int,
)

data class StreakResponse(val type: StreakType, val count: Int)

data class WatchLogPageResponse(
    val content: List<WatchLogResponse>,
    val page: Int,
    val size: Int,
    val totalElements: Long,
    val totalPages: Int,
    val last: Boolean,
)

fun WatchLog.toResponse() = WatchLogResponse(
    id = id,
    memberId = memberId,
    gameId = gameId,
    gameSeason = gameSeason,
    venue = venue,
    watchType = watchType,
    cheeringTeam = cheeringTeam,
    result = result,
    title = title,
    content = content,
    seatInfo = seatInfo?.let { SeatInfoResponse(it.grade, it.section, it.row, it.number) },
    companions = companions,
    mood = mood,
    cost = cost?.let { WatchCostResponse(it.ticketCost, it.foodCost, it.transportCost, it.total) },
    weather = weather?.let { WeatherInfoResponse(it.condition, it.temperatureCelsius) },
    isPublic = isPublic,
    images = images.map { WatchLogImageResponse(it.id, it.imageUrl, it.imageType, it.sortOrder) },
    createdAt = createdAt,
    updatedAt = updatedAt,
)

fun WatchStats.toResponse() = WatchStatsResponse(
    totalCount = totalCount,
    stadiumCount = stadiumCount,
    homeCount = homeCount,
    winCount = winCount,
    loseCount = loseCount,
    drawCount = drawCount,
    winRate = winRate,
    stadiumWinRate = stadiumWinRate,
    homeWinRate = homeWinRate,
    currentStreak = StreakResponse(currentStreak.type, currentStreak.count),
    bestWinStreak = bestWinStreak,
    visitedVenues = visitedVenues,
    seasonTotalCost = seasonTotalCost,
)
