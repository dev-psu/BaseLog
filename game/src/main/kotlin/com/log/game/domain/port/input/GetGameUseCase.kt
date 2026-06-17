package com.log.game.domain.port.input

import com.log.common.domain.KboTeam
import com.log.game.domain.model.Game

interface GetGameUseCase {
    fun getBySeasonAndMonth(season: Int, month: Int, favoriteTeam: KboTeam? = null): List<Game>
    fun getById(id: Long): Game
}
