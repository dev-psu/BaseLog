package com.log.watchlog.domain.port.output

import com.log.watchlog.domain.model.WatchLog
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface FeedRepository {
    fun findPublicLogsByMemberIds(memberIds: List<Long>, pageable: Pageable): Page<WatchLog>
}
