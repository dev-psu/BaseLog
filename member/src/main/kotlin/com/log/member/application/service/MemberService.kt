package com.log.member.application.service

import com.log.member.domain.model.Member
import com.log.member.domain.port.input.GetMemberUseCase
import com.log.member.domain.port.input.RegisterMemberCommand
import com.log.member.domain.port.input.RegisterMemberUseCase
import com.log.member.domain.port.output.MemberRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * 회원 유스케이스 구현체
 * port(RegisterMemberUseCase, GetMemberUseCase) -> service(MemberService) -> port(MemberRepository)
 */
@Service
@Transactional
class MemberService(
    private val memberRepository: MemberRepository,
) : RegisterMemberUseCase, GetMemberUseCase {

    override fun register(command: RegisterMemberCommand): Long {
        // 흐름 예시:
        // 1. 이메일 중복 검사 (memberRepository.existsByEmail)
        // 2. Member.create() 로 도메인 객체 생성
        // 3. memberRepository.save() 로 저장 후 id 반환
        TODO("구현 필요")
    }

    @Transactional(readOnly = true)
    override fun getById(id: Long): Member {
        // memberRepository.findById() 후 없으면 예외 던지기
        TODO("구현 필요")
    }
}
