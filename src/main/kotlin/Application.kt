package com.demo

import com.demo.config.ApiConfig
import com.demo.config.CrawlerConfig
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication

@SpringBootApplication
@EnableConfigurationProperties(ApiConfig::class, CrawlerConfig::class)
open class Application

fun main(args: Array<String>) {
    runApplication<Application>(*args)
}
