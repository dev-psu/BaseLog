package com.log.watchlog.domain.port.input

import com.log.watchlog.domain.model.WatchStats

interface GetWatchStatsUseCase {
    fun getMyStats(memberId: Long, season: Int?): WatchStats
}
