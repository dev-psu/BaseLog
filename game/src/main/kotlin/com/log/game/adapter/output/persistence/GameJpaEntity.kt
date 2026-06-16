package com.log.game.adapter.output.persistence

import com.log.common.domain.KboTeam
import com.log.game.domain.model.Game
import com.log.game.domain.model.GameStatus
import com.log.game.domain.model.GameType
import jakarta.persistence.*
import java.time.LocalDate
import java.time.LocalTime

@Entity
@Table(
    name = "game",
    uniqueConstraints = [UniqueConstraint(columnNames = ["season", "game_type", "game_date", "home_team", "away_team"])],
)
class GameJpaEntity(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    val season: Short,

    @Enumerated(EnumType.STRING)
    val gameType: GameType,

    val gameDate: LocalDate,

    val gameTime: LocalTime?,

    @Enumerated(EnumType.STRING)
    val homeTeam: KboTeam,

    @Enumerated(EnumType.STRING)
    val awayTeam: KboTeam,

    val venue: String?,

    val homeScore: Int?,

    val awayScore: Int?,

    @Enumerated(EnumType.STRING)
    val status: GameStatus,

    val gameNumber: Int = 1,
) {
    fun toDomain() = Game(
        id = id,
        season = season.toInt(),
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
}
