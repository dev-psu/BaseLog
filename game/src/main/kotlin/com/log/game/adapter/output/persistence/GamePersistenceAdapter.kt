package com.log.game.adapter.output.persistence

import com.log.game.domain.model.Game
import com.log.game.domain.model.GameDetail
import com.log.game.domain.port.output.GameRepository
import org.springframework.stereotype.Component
import java.time.LocalDate

@Component
class GamePersistenceAdapter(
    private val gameJpaRepository: GameJpaRepository,
    private val gameDetailJpaRepository: GameDetailJpaRepository,
) : GameRepository {

    override fun findBySeasonAndMonth(season: Int, month: Int): List<Game> {
        val start = LocalDate.of(season, month, 1)
        val end = start.plusMonths(1).minusDays(1)
        return gameJpaRepository
            .findBySeasonAndGameDateBetweenOrderByGameDateAscGameTimeAsc(season.toShort(), start, end)
            .map { it.toDomain() }
    }

    override fun findById(id: Long): Game? =
        gameJpaRepository.findById(id).orElse(null)?.toDomain()

    override fun findDetailByGameId(gameId: Long): GameDetail? =
        gameDetailJpaRepository.findById(gameId).orElse(null)?.toDomain()
}
