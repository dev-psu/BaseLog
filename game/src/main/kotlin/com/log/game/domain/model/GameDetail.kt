package com.log.game.domain.model

data class GameDetail(
    val gameId: Long,
    val awayHits: Int,
    val awayErrors: Int,
    val homeHits: Int,
    val homeErrors: Int,
    val awayInnings: List<Int>,
    val homeInnings: List<Int>,
)
