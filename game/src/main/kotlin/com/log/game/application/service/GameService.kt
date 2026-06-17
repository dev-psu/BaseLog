package com.log.game.application.service

import com.log.common.domain.KboTeam
import com.log.game.domain.exception.GameErrorCode
import com.log.game.domain.model.Game
import com.log.game.domain.port.input.GetGameUseCase
import com.log.game.domain.port.output.GameRepository
import com.log.game.domain.port.output.WeatherPort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

@Service
@Transactional(readOnly = true)
class GameService(
    private val gameRepository: GameRepository,
    private val weatherPort: WeatherPort,
) : GetGameUseCase {

    override fun getBySeasonAndMonth(season: Int, month: Int, favoriteTeam: KboTeam?): List<Game> {
        val games = gameRepository.findBySeasonAndMonth(season, month)
            .map { attachWeather(it) }

        return if (favoriteTeam == null) {
            games
        } else {
            val (preferred, others) = games.partition {
                it.homeTeam == favoriteTeam || it.awayTeam == favoriteTeam
            }
            preferred + others
        }
    }

    override fun getById(id: Long): Game {
        val game = gameRepository.findById(id) ?: throw GameErrorCode.GAME_NOT_FOUND.toException()
        val detail = gameRepository.findDetailByGameId(id)
        return attachWeather(game.copy(detail = detail))
    }

    private fun attachWeather(game: Game): Game {
        if (game.gameDate != LocalDate.now() || game.venue == null) return game
        val weather = game.venue.let { weatherPort.getCurrentWeather(it) } ?: return game
        return game.copy(weather = weather)
    }
}
