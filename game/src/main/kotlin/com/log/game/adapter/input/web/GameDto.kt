package com.log.game.adapter.input.web

import com.log.common.domain.KboTeam
import com.log.game.domain.model.*
import java.time.LocalDate
import java.time.LocalTime

data class GameDetailResponse(
    val awayHits: Int,
    val awayErrors: Int,
    val homeHits: Int,
    val homeErrors: Int,
    val awayInnings: List<Int>,
    val homeInnings: List<Int>,
)

data class GameWeatherResponse(
    val condition: WeatherCondition,
    val temperatureCelsius: Double?,
)

data class GameResponse(
    val id: Long,
    val season: Int,
    val gameType: GameType,
    val gameDate: LocalDate,
    val gameTime: LocalTime?,
    val homeTeam: KboTeam,
    val awayTeam: KboTeam,
    val venue: String?,
    val homeScore: Int?,
    val awayScore: Int?,
    val status: GameStatus,
    val gameNumber: Int,
    val detail: GameDetailResponse?,
    val weather: GameWeatherResponse?,
)

fun GameDetail.toResponse() = GameDetailResponse(
    awayHits = awayHits,
    awayErrors = awayErrors,
    homeHits = homeHits,
    homeErrors = homeErrors,
    awayInnings = awayInnings,
    homeInnings = homeInnings,
)

fun GameWeather.toResponse() = GameWeatherResponse(
    condition = condition,
    temperatureCelsius = temperatureCelsius,
)

fun Game.toResponse() = GameResponse(
    id = id,
    season = season,
    gameType = gameType,
    gameDate = gameDate,
    gameTime = gameTime,
    homeTeam = homeTeam,
    awayTeam = awayTeam,
    venue = venue,
    homeScore = homeScore,
    awayScore = awayScore,
    status = status,
    gameNumber = gameNumber,
    detail = detail?.toResponse(),
    weather = weather?.toResponse(),
)
