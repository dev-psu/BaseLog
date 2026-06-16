package com.log.member.application.service

import com.log.member.domain.exception.ErrorCode
import com.log.member.domain.model.Email
import com.log.member.domain.model.Member
import com.log.member.domain.model.Nickname
import com.log.member.domain.model.SocialAccount
import com.log.member.domain.model.SocialProvider
import com.log.member.domain.port.input.GetMemberUseCase
import com.log.member.domain.port.input.RegisterMemberCommand
import com.log.member.domain.port.input.RegisterMemberUseCase
import com.log.member.domain.port.input.UpdateProfileCommand
import com.log.member.domain.port.input.UpdateProfileUseCase
import com.log.member.domain.port.output.MemberRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
@Transactional
class MemberService(
    private val memberRepository: MemberRepository,
) : RegisterMemberUseCase, GetMemberUseCase, UpdateProfileUseCase {

    override fun register(command: RegisterMemberCommand): Long {
        command.email?.let { checkEmailAvailable(it) }
        checkNicknameAvailable(command.nickname)

        val member = Member(
            email = command.email?.let { Email(it) },
            nickname = Nickname(command.nickname),
            favoriteTeam = command.favoriteTeam,
            socialAccounts = listOf(
                SocialAccount(
                    provider = SocialProvider.valueOf(command.provider),
                    providerId = command.providerId,
                )
            ),
        )
        return memberRepository.save(member).id
    }

    @Transactional(readOnly = true)
    override fun getById(id: Long): Member =
        memberRepository.findById(id) ?: throw ErrorCode.MEMBER_NOT_FOUND.toException()

    @Transactional(readOnly = true)
    override fun checkEmailAvailable(email: String) {
        if (memberRepository.existsByEmail(email)) throw ErrorCode.DUPLICATE_EMAIL.toException()
    }

    @Transactional(readOnly = true)
    override fun checkNicknameAvailable(nickname: String) {
        if (memberRepository.existsByNickname(nickname)) throw ErrorCode.DUPLICATE_NICKNAME.toException()
    }

    override fun updateProfile(command: UpdateProfileCommand): Member {
        val member = memberRepository.findById(command.memberId)
            ?: throw ErrorCode.MEMBER_NOT_FOUND.toException()

        command.nickname?.let {
            if (it != member.nickname.value) checkNicknameAvailable(it)
        }

        val updated = member.copy(
            nickname = command.nickname?.let { Nickname(it) } ?: member.nickname,
            profileImageUrl = command.profileImageUrl ?: member.profileImageUrl,
            favoriteTeam = command.favoriteTeam ?: member.favoriteTeam,
            bio = command.bio ?: member.bio,
            isPublic = command.isPublic ?: member.isPublic,
            updatedAt = LocalDateTime.now(),
        )
        return memberRepository.save(updated)
    }
}
