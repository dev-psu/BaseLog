package com.log.game.domain.model

import com.log.common.domain.KboTeam
import java.time.LocalDate
import java.time.LocalTime

data class Game(
    val id: Long = 0,
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
    val gameNumber: Int = 1,
    val detail: GameDetail? = null,
)
