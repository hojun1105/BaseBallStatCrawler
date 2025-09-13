package com.demo.config

import io.swagger.v3.oas.annotations.OpenAPIDefinition
import io.swagger.v3.oas.annotations.info.Info
import org.springframework.context.annotation.Configuration

@Configuration
@OpenAPIDefinition(
    info = Info(
        title = "Demo API",
        version = "v1",
        description = "Spring Boot + Kotlin + Swagger API 명세서"
    )
)
class SwaggerConfig {
}