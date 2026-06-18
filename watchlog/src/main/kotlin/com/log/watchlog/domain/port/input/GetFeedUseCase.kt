package com.log.watchlog.domain.port.input

import com.log.watchlog.domain.model.WatchLog
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface GetFeedUseCase {
    fun getFeed(followingIds: List<Long>, pageable: Pageable): Page<WatchLog>
}
