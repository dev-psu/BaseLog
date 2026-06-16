package com.log.member.domain.port.input

import com.log.member.domain.model.KboTeam
import com.log.member.domain.model.SocialLoginResult
import com.log.member.domain.model.SocialProvider
import com.log.member.domain.model.TokenPair

interface AuthUseCase {
    fun socialLogin(provider: SocialProvider, code: String): SocialLoginResult
    fun completeSignup(onboardingToken: String, nickname: String, favoriteTeam: KboTeam?): TokenPair
    fun refresh(refreshToken: String): TokenPair
    fun logout(refreshToken: String)
}
