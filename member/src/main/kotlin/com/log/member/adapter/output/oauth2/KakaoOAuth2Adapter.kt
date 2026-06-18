package com.log.member.adapter.output.oauth2

import com.log.member.domain.exception.ErrorCode
import com.log.member.domain.model.SocialProvider
import com.log.member.domain.model.SocialUserInfo
import com.log.member.domain.port.output.SocialOAuth2Port
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient
import com.fasterxml.jackson.annotation.JsonProperty

@Component
class KakaoOAuth2Adapter : SocialOAuth2Port {

    override val provider = SocialProvider.KAKAO

    private val restClient = RestClient.create()

    override fun getUserInfo(accessToken: String): SocialUserInfo =
        fetchUserInfo(accessToken)

    private fun fetchUserInfo(accessToken: String): SocialUserInfo {
        val response = try {
            restClient.get()
                .uri(USER_INFO_URL)
                .header("Authorization", "Bearer $accessToken")
                .retrieve()
                .onStatus({ it.isError }) { _, _ -> throw ErrorCode.SOCIAL_AUTH_FAILED.toException() }
                .body(KakaoUserInfoResponse::class.java)
        } catch (e: Exception) {
            throw ErrorCode.SOCIAL_AUTH_FAILED.toException()
        }

        return SocialUserInfo(
            provider = SocialProvider.KAKAO,
            providerId = response?.id?.toString() ?: throw ErrorCode.SOCIAL_AUTH_FAILED.toException(),
            email = response.kakaoAccount?.email,
        )
    }

    private data class KakaoUserInfoResponse(
        val id: Long,
        @JsonProperty("kakao_account") val kakaoAccount: KakaoAccount?,
    ) {
        data class KakaoAccount(val email: String?)
    }

    companion object {
        private const val USER_INFO_URL = "https://kapi.kakao.com/v2/user/me"
    }
}
