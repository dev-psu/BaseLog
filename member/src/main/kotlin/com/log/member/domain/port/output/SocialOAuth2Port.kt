package com.log.member.domain.port.output

import com.log.member.domain.model.SocialProvider
import com.log.member.domain.model.SocialUserInfo

interface SocialOAuth2Port {
    val provider: SocialProvider
    fun getUserInfo(code: String): SocialUserInfo
}
