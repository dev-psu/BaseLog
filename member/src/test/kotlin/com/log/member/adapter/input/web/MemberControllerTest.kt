package com.log.member.adapter.input.web

import com.log.common.exception.GlobalExceptionHandler
import com.log.member.domain.exception.ErrorCode
import com.log.member.domain.model.Email
import com.log.member.domain.model.Member
import com.log.member.domain.model.Nickname
import com.log.member.domain.model.SocialAccount
import com.log.member.domain.model.SocialProvider
import com.log.member.domain.port.input.GetMemberUseCase
import com.log.member.domain.port.input.RegisterMemberUseCase
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.delete
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean

@ExtendWith(MockitoExtension::class)
class MemberControllerTest {

    @Mock private lateinit var registerMemberUseCase: RegisterMemberUseCase
    @Mock private lateinit var getMemberUseCase: GetMemberUseCase

    @InjectMocks private lateinit var memberController: MemberController

    private lateinit var mockMvc: MockMvc

    @BeforeEach
    fun setup() {
        val validator = LocalValidatorFactoryBean().also { it.afterPropertiesSet() }
        mockMvc = MockMvcBuilders
            .standaloneSetup(memberController)
            .setControllerAdvice(GlobalExceptionHandler())
            .setValidator(validator)
            .build()
    }

    @Nested
    inner class Register {

        @Test
        fun `정상 가입 시 200과 회원 ID를 반환한다`() {
            whenever(registerMemberUseCase.register(any())).thenReturn(1L)

            mockMvc.post("/api/members") {
                contentType = MediaType.APPLICATION_JSON
                content = """{"email":"test@test.com","nickname":"테스터","provider":"KAKAO","providerId":"kakao_123"}"""
            }.andExpect {
                status { isOk() }
                jsonPath("$.success") { value(true) }
                jsonPath("$.data") { value(1) }
            }
        }

        @Test
        fun `이메일 형식이 잘못된 경우 400을 반환한다`() {
            mockMvc.post("/api/members") {
                contentType = MediaType.APPLICATION_JSON
                content = """{"email":"invalid-email","nickname":"테스터","provider":"KAKAO","providerId":"kakao_123"}"""
            }.andExpect {
                status { isBadRequest() }
                jsonPath("$.success") { value(false) }
                jsonPath("$.error.code") { value("INVALID_INPUT") }
            }
        }

        @Test
        fun `닉네임이 2자 미만이면 400을 반환한다`() {
            mockMvc.post("/api/members") {
                contentType = MediaType.APPLICATION_JSON
                content = """{"email":"test@test.com","nickname":"a","provider":"KAKAO","providerId":"kakao_123"}"""
            }.andExpect {
                status { isBadRequest() }
                jsonPath("$.error.code") { value("INVALID_INPUT") }
            }
        }

        @Test
        fun `이메일 중복 시 409를 반환한다`() {
            whenever(registerMemberUseCase.register(any()))
                .thenThrow(ErrorCode.DUPLICATE_EMAIL.toException())

            mockMvc.post("/api/members") {
                contentType = MediaType.APPLICATION_JSON
                content = """{"email":"test@test.com","nickname":"테스터","provider":"KAKAO","providerId":"kakao_123"}"""
            }.andExpect {
                status { isConflict() }
                jsonPath("$.error.code") { value("DUPLICATE_EMAIL") }
            }
        }

        @Test
        fun `닉네임 중복 시 409를 반환한다`() {
            whenever(registerMemberUseCase.register(any()))
                .thenThrow(ErrorCode.DUPLICATE_NICKNAME.toException())

            mockMvc.post("/api/members") {
                contentType = MediaType.APPLICATION_JSON
                content = """{"email":"test@test.com","nickname":"테스터","provider":"KAKAO","providerId":"kakao_123"}"""
            }.andExpect {
                status { isConflict() }
                jsonPath("$.error.code") { value("DUPLICATE_NICKNAME") }
            }
        }

        @Test
        fun `JSON 형식이 잘못된 경우 400을 반환한다`() {
            mockMvc.post("/api/members") {
                contentType = MediaType.APPLICATION_JSON
                content = """{"invalid json"""
            }.andExpect {
                status { isBadRequest() }
                jsonPath("$.error.code") { value("INVALID_INPUT") }
            }
        }
    }

    @Nested
    inner class GetById {

        @Test
        fun `정상 조회 시 200과 회원 정보를 반환한다`() {
            whenever(getMemberUseCase.getById(1L)).thenReturn(createMember(id = 1L))

            mockMvc.get("/api/members/1").andExpect {
                status { isOk() }
                jsonPath("$.success") { value(true) }
                jsonPath("$.data.id") { value(1) }
                jsonPath("$.data.email") { value("test@test.com") }
                jsonPath("$.data.nickname") { value("테스터") }
            }
        }

        @Test
        fun `존재하지 않는 회원 조회 시 404를 반환한다`() {
            whenever(getMemberUseCase.getById(999L))
                .thenThrow(ErrorCode.MEMBER_NOT_FOUND.toException())

            mockMvc.get("/api/members/999").andExpect {
                status { isNotFound() }
                jsonPath("$.error.code") { value("MEMBER_NOT_FOUND") }
            }
        }

        @Test
        fun `ID에 문자열을 전달하면 400을 반환한다`() {
            mockMvc.get("/api/members/invalid-id").andExpect {
                status { isBadRequest() }
                jsonPath("$.error.code") { value("INVALID_TYPE") }
            }
        }

        @Test
        fun `예상치 못한 예외 발생 시 500을 반환한다`() {
            whenever(getMemberUseCase.getById(1L))
                .thenThrow(RuntimeException("unexpected"))

            mockMvc.get("/api/members/1").andExpect {
                status { isInternalServerError() }
                jsonPath("$.error.code") { value("INTERNAL_SERVER_ERROR") }
            }
        }
    }

    @Nested
    inner class CheckEmail {

        @Test
        fun `사용 가능한 이메일이면 200을 반환한다`() {
            mockMvc.get("/api/members/check/email") {
                param("email", "available@test.com")
            }.andExpect {
                status { isOk() }
                jsonPath("$.success") { value(true) }
            }
        }

        @Test
        fun `중복된 이메일이면 409를 반환한다`() {
            whenever(getMemberUseCase.checkEmailAvailable(any()))
                .thenThrow(ErrorCode.DUPLICATE_EMAIL.toException())

            mockMvc.get("/api/members/check/email") {
                param("email", "test@test.com")
            }.andExpect {
                status { isConflict() }
                jsonPath("$.error.code") { value("DUPLICATE_EMAIL") }
            }
        }

        @Test
        fun `email 파라미터가 없으면 400을 반환한다`() {
            mockMvc.get("/api/members/check/email").andExpect {
                status { isBadRequest() }
                jsonPath("$.error.code") { value("MISSING_PARAMETER") }
            }
        }
    }

    @Nested
    inner class CheckNickname {

        @Test
        fun `사용 가능한 닉네임이면 200을 반환한다`() {
            mockMvc.get("/api/members/check/nickname") {
                param("nickname", "사용가능")
            }.andExpect {
                status { isOk() }
                jsonPath("$.success") { value(true) }
            }
        }

        @Test
        fun `중복된 닉네임이면 409를 반환한다`() {
            whenever(getMemberUseCase.checkNicknameAvailable(any()))
                .thenThrow(ErrorCode.DUPLICATE_NICKNAME.toException())

            mockMvc.get("/api/members/check/nickname") {
                param("nickname", "테스터")
            }.andExpect {
                status { isConflict() }
                jsonPath("$.error.code") { value("DUPLICATE_NICKNAME") }
            }
        }

        @Test
        fun `nickname 파라미터가 없으면 400을 반환한다`() {
            mockMvc.get("/api/members/check/nickname").andExpect {
                status { isBadRequest() }
                jsonPath("$.error.code") { value("MISSING_PARAMETER") }
            }
        }
    }

    @Nested
    inner class ExceptionHandling {

        @Test
        fun `지원하지 않는 HTTP 메서드는 405를 반환한다`() {
            mockMvc.delete("/api/members").andExpect {
                status { isMethodNotAllowed() }
                jsonPath("$.error.code") { value("METHOD_NOT_ALLOWED") }
            }
        }
    }

    private fun createMember(id: Long = 1L) = Member(
        id = id,
        email = Email("test@test.com"),
        nickname = Nickname("테스터"),
        socialAccounts = listOf(SocialAccount(SocialProvider.KAKAO, "kakao_123")),
    )
}
