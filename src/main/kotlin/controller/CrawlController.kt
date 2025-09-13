package com.demo.controller

import com.demo.service.*
import io.swagger.v3.oas.annotations.Operation
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/crawl")
class CrawlController(
    private val crawlHitterStatService: CrawlHitterStatService,
    private val crawlPitcherStatService: CrawlPitcherStatService,
    private val saveHitterStatService: SaveHitterStatService,
    private val savePitcherStatService: SavePitcherStatService,
) {

    @Operation(summary = "타자 크롤링")
    @PostMapping("/hitter")
    fun startHitterCrawl(@RequestParam teamName: String): String {
        val data = crawlHitterStatService.invoke(teamName)
        println("$teamName 크롤링완료")
        saveHitterStatService.invoke(data)
        println("$teamName 저장완료")

        return "$teamName 크롤링 및 저장 완료"
    }

    @Operation(summary = "투수 크롤링")
    @PostMapping("/pitcher")
    fun startPitcherCrawl(@RequestParam teamName: String): String {
        val data = crawlPitcherStatService.invoke(teamName)
        println("$teamName 크롤링완료")
        savePitcherStatService.invoke(data)
        println("$teamName 저장완료")

        return "$teamName 크롤링 및 저장 완료"
    }
}
