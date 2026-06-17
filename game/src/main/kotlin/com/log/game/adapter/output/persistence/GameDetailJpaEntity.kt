package com.log.game.adapter.output.persistence

import com.log.game.domain.model.GameDetail
import jakarta.persistence.*

@Entity
@Table(name = "game_detail")
class GameDetailJpaEntity(

    @Id
    val gameId: Long,

    val awayHits: Int,

    val awayErrors: Int,

    val homeHits: Int,

    val homeErrors: Int,

    @Column(columnDefinition = "JSON")
    @Convert(converter = InningsConverter::class)
    val innings: InningsData,
) {
    fun toDomain() = GameDetail(
        gameId = gameId,
        awayHits = awayHits,
        awayErrors = awayErrors,
        homeHits = homeHits,
        homeErrors = homeErrors,
        awayInnings = innings.away,
        homeInnings = innings.home,
    )
}
