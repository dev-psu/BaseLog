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
import com.log.member.domain.port.output.MemberRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class MemberService(
    private val memberRepository: MemberRepository,
) : RegisterMemberUseCase, GetMemberUseCase {

    override fun register(command: RegisterMemberCommand): Long {
        checkEmailAvailable(command.email)
        checkNicknameAvailable(command.nickname)

        val member = Member(
            email = Email(command.email),
            nickname = Nickname(command.nickname),
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
}
