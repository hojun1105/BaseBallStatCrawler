package com.demo.controller

import com.demo.model.NaverStore
import com.demo.repository.NaverStoreRepository
import com.demo.service.NaverMapService
import io.swagger.v3.oas.annotations.Operation
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/naver-map")
class NaverMapController(
    private val naverMapService: NaverMapService,
    private val naverStoreRepository: NaverStoreRepository
) {

    @Operation(summary = "네이버 지도 크롤링", description = "특정 검색어로 네이버 지도에서 가게 정보를 크롤링합니다.")
    @PostMapping("/crawl")
    fun crawlStores(
        @RequestParam searchKeyword: String,
        @RequestParam locationName: String,
        @RequestParam(required = false, defaultValue = "10") maxStoresPerPage: Int
    ): Map<String, Any> {
        val stores = naverMapService.crawlAndSave(searchKeyword, locationName, maxStoresPerPage)
        return mapOf(
            "success" to true,
            "message" to "${locationName} 크롤링 완료",
            "count" to stores.size,
            "stores" to stores
        )
    }

    @Operation(summary = "야구장 주변 크롤링", description = "야구장 주변 술집 정보를 크롤링합니다.")
    @PostMapping("/crawl/baseball-stadiums")
    fun crawlBaseballStadiums(): Map<String, Any> {
        val tasks = listOf(
            "잠실새내역 술집" to "잠실야구장",
            "수원KT위즈파크 술집" to "수원KT위즈파크",
            "선학역 술집" to "인천SSG랜더스필드",
            "마산역 술집" to "창원NC파크",
            "광주기아챔피언스필드 술집" to "광주기아챔피언스필드",
            "사직야구장 술집" to "사직야구장",
            "고산역 술집" to "대구삼성라이온즈파크",
            "중앙로 술집" to "대전한화생명이글스파크",
            "구일역 술집" to "고척스카이돔"
        )
        
        val results = naverMapService.crawlAllLocations(tasks)
        val totalCount = results.values.sumOf { it.size }
        
        return mapOf(
            "success" to true,
            "message" to "모든 야구장 주변 크롤링 완료",
            "totalCount" to totalCount,
            "results" to results.mapKeys { it.key }
        )
    }

    @Operation(summary = "좌표 변환", description = "모든 가게의 주소를 좌표로 변환하여 업데이트합니다.")
    @PostMapping("/geocode/update-all")
    fun updateAllCoordinates(): Map<String, Any> {
        naverMapService.updateAllCoordinates()
        return mapOf(
            "success" to true,
            "message" to "좌표 변환 작업이 완료되었습니다."
        )
    }

    @Operation(summary = "크롤링된 가게 목록 조회", description = "특정 위치의 크롤링된 가게 목록을 조회합니다.")
    @GetMapping("/stores")
    fun getStores(@RequestParam(required = false) location: String?): List<NaverStore> {
        return if (location != null) {
            naverStoreRepository.findByLocation(location)
        } else {
            naverStoreRepository.findAll()
        }
    }
}

