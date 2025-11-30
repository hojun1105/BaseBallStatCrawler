package com.demo.config

import org.springframework.boot.context.properties.ConfigurationProperties

/**
 * 네이버 클라우드 플랫폼 API 인증 설정
 */
@ConfigurationProperties(prefix = "naver.api")
class ApiConfig {
    var clientId: String = ""
    var clientSecret: String = ""
}

