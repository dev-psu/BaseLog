package com.log.game.adapter.output.persistence

import org.springframework.data.jpa.repository.JpaRepository
import java.time.LocalDate

interface GameJpaRepository : JpaRepository<GameJpaEntity, Long> {

    fun findBySeasonAndGameDateBetweenOrderByGameDateAscGameTimeAsc(
        season: Short,
        start: LocalDate,
        end: LocalDate,
    ): List<GameJpaEntity>
}
