package com.log.watchlog.adapter.output.persistence

import com.log.watchlog.domain.model.WatchLog
import com.log.watchlog.domain.port.output.FeedRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Component

@Component
class FeedPersistenceAdapter(
    private val watchLogJpaRepository: WatchLogJpaRepository,
) : FeedRepository {

    override fun findPublicLogsByMemberIds(memberIds: List<Long>, pageable: Pageable): Page<WatchLog> =
        watchLogJpaRepository.findPublicLogsByMemberIds(memberIds, pageable).map { it.toDomain() }
}
