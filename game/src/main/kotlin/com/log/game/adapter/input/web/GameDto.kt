package com.log.game.adapter.input.web

import com.log.common.domain.KboTeam
import com.log.game.domain.model.Game
import com.log.game.domain.model.GameStatus
import com.log.game.domain.model.GameType
import java.time.LocalDate
import java.time.LocalTime

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
)
