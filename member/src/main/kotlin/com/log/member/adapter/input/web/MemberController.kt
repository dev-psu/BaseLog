package com.log.member.adapter.input.web

import com.log.common.response.ApiResponse
import com.log.member.domain.port.input.GetMemberUseCase
import com.log.member.domain.port.input.RegisterMemberCommand
import com.log.member.domain.port.input.RegisterMemberUseCase
import com.log.member.domain.port.input.UpdateProfileCommand
import com.log.member.domain.port.input.UpdateProfileUseCase
import jakarta.validation.Valid
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/members")
class MemberController(
    private val registerMemberUseCase: RegisterMemberUseCase,
    private val getMemberUseCase: GetMemberUseCase,
    private val updateProfileUseCase: UpdateProfileUseCase,
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

    @GetMapping("/me")
    fun getMe(authentication: Authentication): ApiResponse<MemberResponse> {
        val memberId = authentication.principal as Long
        val member = getMemberUseCase.getById(memberId)
        return ApiResponse.ok(member.toResponse())
    }

    @PatchMapping("/me")
    fun updateMe(
        authentication: Authentication,
        @Valid @RequestBody request: UpdateProfileRequest,
    ): ApiResponse<MemberResponse> {
        val memberId = authentication.principal as Long
        val command = UpdateProfileCommand(
            memberId = memberId,
            nickname = request.nickname,
            profileImageUrl = request.profileImageUrl,
            favoriteTeam = request.favoriteTeam,
            bio = request.bio,
            isPublic = request.isPublic,
        )
        val member = updateProfileUseCase.updateProfile(command)
        return ApiResponse.ok(member.toResponse())
    }
}
