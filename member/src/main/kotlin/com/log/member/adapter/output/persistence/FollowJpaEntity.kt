package com.log.member.adapter.output.persistence

import com.log.member.domain.model.Follow
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(
    name = "follows",
    uniqueConstraints = [UniqueConstraint(columnNames = ["follower_id", "following_id"])],
)
class FollowJpaEntity(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(name = "follower_id", nullable = false)
    val followerId: Long,

    @Column(name = "following_id", nullable = false)
    val followingId: Long,

    @Column(nullable = false)
    val createdAt: LocalDateTime,
) {
    fun toDomain() = Follow(id = id, followerId = followerId, followingId = followingId, createdAt = createdAt)

    companion object {
        fun fromDomain(follow: Follow) = FollowJpaEntity(
            id = follow.id,
            followerId = follow.followerId,
            followingId = follow.followingId,
            createdAt = follow.createdAt,
        )
    }
}
