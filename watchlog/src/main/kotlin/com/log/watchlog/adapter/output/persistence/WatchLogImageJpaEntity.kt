package com.log.watchlog.adapter.output.persistence

import com.log.watchlog.domain.model.ImageType
import com.log.watchlog.domain.model.WatchLogImage
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "watch_log_image")
class WatchLogImageJpaEntity(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "watch_log_id", nullable = false)
    val watchLog: WatchLogJpaEntity,

    val imageUrl: String,

    @Enumerated(EnumType.STRING)
    val imageType: ImageType,

    val sortOrder: Int,

    val createdAt: LocalDateTime,
) {
    fun toDomain() = WatchLogImage(
        id = id,
        imageUrl = imageUrl,
        imageType = imageType,
        sortOrder = sortOrder,
    )
}
