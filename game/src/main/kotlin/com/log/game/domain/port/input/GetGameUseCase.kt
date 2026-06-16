package com.log.game.domain.port.input

import com.log.game.domain.model.Game

interface GetGameUseCase {
    fun getBySeasonAndMonth(season: Int, month: Int): List<Game>
    fun getById(id: Long): Game
}
