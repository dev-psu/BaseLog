package com.log.member.adapter.output.persistence

import com.log.member.domain.model.*
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "members")
class MemberJpaEntity(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    val email: String?,

    val nickname: String,

    @Enumerated(EnumType.STRING)
    val role: MemberRole,

    @Enumerated(EnumType.STRING)
    val status: MemberStatus,

    val profileImageUrl: String? = null,

    @Enumerated(EnumType.STRING)
    val favoriteTeam: KboTeam? = null,

    @Column(length = 100)
    val bio: String? = null,

    val isPublic: Boolean = true,

    val createdAt: LocalDateTime,

    val updatedAt: LocalDateTime,

    // SocialAccount는 독립 생명주기가 없는 VO이므로 @Entity 대신 @ElementCollection으로 관리
    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "members_social_accounts", joinColumns = [JoinColumn(name = "member_id")])
    val socialAccounts: MutableList<SocialAccountEmbeddable> = mutableListOf(),
) {
    fun toDomain(): Member = Member(
        id = id,
        email = email?.let { Email(it) },
        nickname = Nickname(nickname),
        role = role,
        status = status,
        socialAccounts = socialAccounts.map { SocialAccount(it.provider, it.providerId) },
        profileImageUrl = profileImageUrl,
        favoriteTeam = favoriteTeam,
        bio = bio,
        isPublic = isPublic,
        createdAt = createdAt,
        updatedAt = updatedAt,
    )

    companion object {
        fun fromDomain(member: Member): MemberJpaEntity = MemberJpaEntity(
            id = member.id,
            email = member.email?.value,
            nickname = member.nickname.value,
            role = member.role,
            status = member.status,
            profileImageUrl = member.profileImageUrl,
            favoriteTeam = member.favoriteTeam,
            bio = member.bio,
            isPublic = member.isPublic,
            createdAt = member.createdAt,
            updatedAt = member.updatedAt,
            socialAccounts = member.socialAccounts
                .map { SocialAccountEmbeddable(it.provider, it.providerId) }
                .toMutableList(),
        )
    }
}

@Embeddable
class SocialAccountEmbeddable(

    @Enumerated(EnumType.STRING)
    val provider: SocialProvider,

    val providerId: String,
)
