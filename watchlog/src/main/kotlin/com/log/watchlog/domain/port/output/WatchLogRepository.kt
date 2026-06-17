package com.log.watchlog.domain.port.output

import com.log.watchlog.domain.model.WatchLog
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface WatchLogRepository {
    fun save(watchLog: WatchLog): WatchLog
    fun findById(id: Long): WatchLog?
    fun findByMemberId(memberId: Long, pageable: Pageable): Page<WatchLog>
    fun findAllByMemberId(memberId: Long): List<WatchLog>
    fun findAllByMemberIdAndSeason(memberId: Long, season: Int): List<WatchLog>
    fun delete(watchLog: WatchLog)
}
