package com.log.watchlog.domain.port.input

import com.log.watchlog.domain.model.WatchLog
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface GetWatchLogUseCase {
    fun getById(watchLogId: Long, requesterId: Long?): WatchLog
    fun getMyLogs(memberId: Long, pageable: Pageable): Page<WatchLog>
}
