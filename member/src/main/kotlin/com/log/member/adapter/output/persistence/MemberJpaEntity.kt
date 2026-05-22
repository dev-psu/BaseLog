package com.log.member.adapter.output.persistence

import com.log.member.domain.model.*
import jakarta.persistence.*
import java.time.LocalDateTime

/**
 * Member Entity
 */
@Entity
@Table(name = "members")
class MemberJpaEntity(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    val email: String,

    val nickname: String,

    @Enumerated(EnumType.STRING)
    val role: MemberRole,

    @Enumerated(EnumType.STRING)
    val status: MemberStatus,

    val createdAt: LocalDateTime,

    val updatedAt: LocalDateTime,

    // SocialAccount 는 별도 테이블로 관리 (members_social_accounts)
    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "members_social_accounts", joinColumns = [JoinColumn(name = "member_id")])
    val socialAccounts: MutableList<SocialAccountEmbeddable> = mutableListOf(),
) {
    fun toDomain(): Member = Member(
        id = id,
        email = Email(email),
        nickname = Nickname(nickname),
        role = role,
        status = status,
        socialAccounts = socialAccounts.map { SocialAccount(it.provider, it.providerId) },
        createdAt = createdAt,
        updatedAt = updatedAt,
    )

    companion object {
        fun fromDomain(member: Member): MemberJpaEntity = MemberJpaEntity(
            id = member.id,
            email = member.email.value,
            nickname = member.nickname.value,
            role = member.role,
            status = member.status,
            createdAt = member.createdAt,
            updatedAt = member.updatedAt,
            socialAccounts = member.socialAccounts
                .map { SocialAccountEmbeddable(it.provider, it.providerId) }
                .toMutableList(),
        )
    }
}

// SocialAccount 를 별도 테이블에 저장하기 위한 Embeddable 객체
@Embeddable
class SocialAccountEmbeddable(

    @Enumerated(EnumType.STRING)
    val provider: SocialProvider,

    val providerId: String,
)
