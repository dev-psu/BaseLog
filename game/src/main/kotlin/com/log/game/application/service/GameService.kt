package com.log.game.application.service

import com.log.game.domain.exception.GameErrorCode
import com.log.game.domain.model.Game
import com.log.game.domain.port.input.GetGameUseCase
import com.log.game.domain.port.output.GameRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class GameService(
    private val gameRepository: GameRepository,
) : GetGameUseCase {

    override fun getBySeasonAndMonth(season: Int, month: Int): List<Game> =
        gameRepository.findBySeasonAndMonth(season, month)

    override fun getById(id: Long): Game {
        val game = gameRepository.findById(id) ?: throw GameErrorCode.GAME_NOT_FOUND.toException()
        val detail = gameRepository.findDetailByGameId(id)
        return game.copy(detail = detail)
    }
}
