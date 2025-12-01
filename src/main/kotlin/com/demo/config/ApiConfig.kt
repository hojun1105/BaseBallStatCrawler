package com.demo.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties(prefix = "naver.api")
class ApiConfig {
    var clientId: String = ""
    var clientSecret: String = ""
}

