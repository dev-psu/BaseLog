package com.log.game.domain.port.output

import com.log.game.domain.model.Game
import com.log.game.domain.model.GameDetail

interface GameRepository {
    fun findBySeasonAndMonth(season: Int, month: Int): List<Game>
    fun findById(id: Long): Game?
    fun findDetailByGameId(gameId: Long): GameDetail?
}
