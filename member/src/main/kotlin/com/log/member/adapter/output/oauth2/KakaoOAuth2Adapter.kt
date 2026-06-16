package com.log.member.adapter.output.oauth2

import com.log.member.domain.exception.ErrorCode
import com.log.member.domain.model.SocialProvider
import com.log.member.domain.model.SocialUserInfo
import com.log.member.domain.port.output.SocialOAuth2Port
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.util.LinkedMultiValueMap
import org.springframework.web.client.RestClient
import com.fasterxml.jackson.annotation.JsonProperty

@Component
class KakaoOAuth2Adapter(
    @Value("\${oauth2.kakao.client-id}") private val clientId: String,
    @Value("\${oauth2.kakao.client-secret}") private val clientSecret: String,
    @Value("\${oauth2.kakao.redirect-uri}") private val redirectUri: String,
) : SocialOAuth2Port {

    override val provider = SocialProvider.KAKAO

    private val restClient = RestClient.create()

    override fun getUserInfo(code: String): SocialUserInfo {
        val accessToken = exchangeCodeForToken(code)
        return fetchUserInfo(accessToken)
    }

    private fun exchangeCodeForToken(code: String): String {
        val params = LinkedMultiValueMap<String, String>().apply {
            add("grant_type", "authorization_code")
            add("client_id", clientId)
            add("client_secret", clientSecret)
            add("redirect_uri", redirectUri)
            add("code", code)
        }

        // 네트워크 오류, 타임아웃, HTTP 오류(onStatus) 모두 SOCIAL_AUTH_FAILED로 통일
        val response = try {
            restClient.post()
                .uri(TOKEN_URL)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(params)
                .retrieve()
                .onStatus({ it.isError }) { _, _ -> throw ErrorCode.SOCIAL_AUTH_FAILED.toException() }
                .body(KakaoTokenResponse::class.java)
        } catch (e: Exception) {
            throw ErrorCode.SOCIAL_AUTH_FAILED.toException()
        }

        return response?.accessToken ?: throw ErrorCode.SOCIAL_AUTH_FAILED.toException()
    }

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

    private data class KakaoTokenResponse(
        @JsonProperty("access_token") val accessToken: String,
    )

    private data class KakaoUserInfoResponse(
        val id: Long,
        @JsonProperty("kakao_account") val kakaoAccount: KakaoAccount?,
    ) {
        data class KakaoAccount(val email: String?)
    }

    companion object {
        private const val TOKEN_URL = "https://kauth.kakao.com/oauth/token"
        private const val USER_INFO_URL = "https://kapi.kakao.com/v2/user/me"
    }
}
