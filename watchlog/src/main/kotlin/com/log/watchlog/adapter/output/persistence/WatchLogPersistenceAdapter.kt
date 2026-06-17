package com.log.watchlog.adapter.output.persistence

import com.log.watchlog.domain.model.WatchLog
import com.log.watchlog.domain.port.output.WatchLogRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class WatchLogPersistenceAdapter(
    private val watchLogJpaRepository: WatchLogJpaRepository,
) : WatchLogRepository {

    override fun save(watchLog: WatchLog): WatchLog {
        val entity = WatchLogJpaEntity.fromDomain(watchLog)
        val saved = watchLogJpaRepository.save(entity)

        val images = watchLog.images.mapIndexed { index, image ->
            WatchLogImageJpaEntity(
                watchLog = saved,
                imageUrl = image.imageUrl,
                imageType = image.imageType,
                sortOrder = image.sortOrder,
                createdAt = LocalDateTime.now(),
            )
        }
        saved.images.clear()
        saved.images.addAll(images)

        return watchLogJpaRepository.save(saved).toDomain()
    }

    override fun findById(id: Long): WatchLog? =
        watchLogJpaRepository.findById(id).orElse(null)?.toDomain()

    override fun findByMemberId(memberId: Long, pageable: Pageable): Page<WatchLog> =
        watchLogJpaRepository.findByMemberIdOrderByCreatedAtDesc(memberId, pageable).map { it.toDomain() }

    override fun findAllByMemberId(memberId: Long): List<WatchLog> =
        watchLogJpaRepository.findAllByMemberIdOrderByCreatedAtDesc(memberId).map { it.toDomain() }

    override fun findAllByMemberIdAndSeason(memberId: Long, season: Int): List<WatchLog> =
        watchLogJpaRepository.findAllByMemberIdAndSeason(memberId, season.toShort()).map { it.toDomain() }

    override fun delete(watchLog: WatchLog) =
        watchLogJpaRepository.deleteById(watchLog.id)
}
