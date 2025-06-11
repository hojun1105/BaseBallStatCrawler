package com.demo.controller

import com.demo.service.CrawlHitterStatService
import com.demo.service.CrawlService
import com.demo.service.SaveHitterStatService
import com.demo.service.SaveService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/crawl")
class CrawlController(
    private val crawlHitterStatService: CrawlHitterStatService,
    private val saveHitterStatService: SaveHitterStatService
) {

    @GetMapping
    fun startCrawl(@RequestParam teamName: String): String {
        val data = crawlHitterStatService.invoke(teamName)
        println("$teamName 크롤링완료")
        saveHitterStatService.invoke(data)
        println("$teamName 저장완료")

        return "$teamName 크롤링 및 저장 완료"
    }
}
