package com.log.watchlog.application.service

import com.log.watchlog.domain.model.WatchLog
import com.log.watchlog.domain.port.input.GetFeedUseCase
import com.log.watchlog.domain.port.output.FeedRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class FeedService(
    private val feedRepository: FeedRepository,
) : GetFeedUseCase {

    override fun getFeed(followingIds: List<Long>, pageable: Pageable): Page<WatchLog> {
        if (followingIds.isEmpty()) return Page.empty(pageable)
        return feedRepository.findPublicLogsByMemberIds(followingIds, pageable)
    }
}
