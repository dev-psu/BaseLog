package com.log.game.adapter.output.persistence

import org.springframework.data.jpa.repository.JpaRepository

interface GameDetailJpaRepository : JpaRepository<GameDetailJpaEntity, Long>
