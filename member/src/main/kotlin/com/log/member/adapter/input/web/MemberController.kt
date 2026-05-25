package com.log.member.adapter.input.web

import com.log.common.response.ApiResponse
import com.log.member.domain.port.input.GetMemberUseCase
import com.log.member.domain.port.input.RegisterMemberCommand
import com.log.member.domain.port.input.RegisterMemberUseCase
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.*

/**
 * 회원 API 컨트롤러 (입력 어댑터)
 * HTTP -> adapter(MemberController) -> port(UseCase)
 */
@RestController
@RequestMapping("/api/members")
class MemberController(
    private val registerMemberUseCase: RegisterMemberUseCase,
    private val getMemberUseCase: GetMemberUseCase,
) {

    @PostMapping
    fun register(@Valid @RequestBody request: RegisterMemberRequest): ApiResponse<Long> {
        val command = RegisterMemberCommand(
            email = request.email,
            nickname = request.nickname,
            provider = request.provider,
            providerId = request.providerId,
        )
        val id = registerMemberUseCase.register(command)
        return ApiResponse.ok(id)
    }

    @GetMapping("/check/email")
    fun checkEmail(@RequestParam email: String): ApiResponse<Unit> {
        getMemberUseCase.checkEmailAvailable(email)
        return ApiResponse.ok(Unit)
    }

    @GetMapping("/check/nickname")
    fun checkNickname(@RequestParam nickname: String): ApiResponse<Unit> {
        getMemberUseCase.checkNicknameAvailable(nickname)
        return ApiResponse.ok(Unit)
    }

    @GetMapping("/{id}")
    fun getById(@PathVariable id: Long): ApiResponse<MemberResponse> {
        val member = getMemberUseCase.getById(id)
        val response = MemberResponse(
            id = member.id,
            email = member.email.value,
            nickname = member.nickname.value,
        )
        return ApiResponse.ok(response)
    }
}
