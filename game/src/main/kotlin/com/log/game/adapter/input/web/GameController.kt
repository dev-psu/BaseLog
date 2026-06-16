package com.log.game.adapter.input.web

import com.log.common.response.ApiResponse
import com.log.game.domain.port.input.GetGameUseCase
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/games")
class GameController(
    private val getGameUseCase: GetGameUseCase,
) {

    @GetMapping
    fun getGames(
        @RequestParam season: Int,
        @RequestParam month: Int,
    ): ApiResponse<List<GameResponse>> {
        val games = getGameUseCase.getBySeasonAndMonth(season, month)
        return ApiResponse.ok(games.map { it.toResponse() })
    }

    @GetMapping("/{id}")
    fun getGame(@PathVariable id: Long): ApiResponse<GameResponse> {
        val game = getGameUseCase.getById(id)
        return ApiResponse.ok(game.toResponse())
    }
}
