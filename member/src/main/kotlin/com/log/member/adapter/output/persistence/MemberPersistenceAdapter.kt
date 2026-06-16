package com.log.member.adapter.output.persistence

import com.log.member.domain.model.Member
import com.log.member.domain.model.SocialProvider
import com.log.member.domain.port.output.MemberRepository
import org.springframework.stereotype.Component

@Component
class MemberPersistenceAdapter(
    private val memberJpaRepository: MemberJpaRepository,
) : MemberRepository {

    override fun save(member: Member): Member {
        val entity = MemberJpaEntity.fromDomain(member)
        return memberJpaRepository.save(entity).toDomain()
    }

    override fun findById(id: Long): Member? =
        memberJpaRepository.findById(id).orElse(null)?.toDomain()

    override fun findByEmail(email: String): Member? =
        memberJpaRepository.findByEmail(email)?.toDomain()

    override fun findBySocialAccount(provider: SocialProvider, providerId: String): Member? =
        memberJpaRepository.findBySocialAccount(provider, providerId)?.toDomain()

    override fun existsByNickname(nickName: String): Boolean =
        memberJpaRepository.existsByNickname(nickName)

    override fun existsByEmail(email: String): Boolean =
        memberJpaRepository.existsByEmail(email)
}
