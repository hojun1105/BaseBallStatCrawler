package com.demo.service

import com.demo.model.NaverStore
import com.demo.repository.NaverStoreRepository
import kotlinx.coroutines.runBlocking
import org.openqa.selenium.WebDriver
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.chrome.ChromeOptions
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * 네이버 지도 크롤링 및 데이터 관리 서비스
 */
@Service
class NaverMapService(
    private val naverMapCrawlerService: NaverMapCrawlerService,
    private val geocodingService: GeocodingService,
    private val naverStoreRepository: NaverStoreRepository
) {

    /**
     * 특정 검색어로 크롤링을 실행하고 데이터베이스에 저장합니다.
     */
    @Transactional
    fun crawlAndSave(searchKeyword: String, locationName: String, maxStoresPerPage: Int = 10): List<NaverStore> {
        val driver = createDriver()
        return try {
            val stores = naverMapCrawlerService.scrape(driver, searchKeyword, locationName, maxStoresPerPage)
            saveStores(stores)
            stores
        } finally {
            driver.quit()
        }
    }

    /**
     * 여러 장소를 순차적으로 크롤링합니다.
     */
    @Transactional
    fun crawlAllLocations(tasks: List<Pair<String, String>>): Map<String, List<NaverStore>> {
        val results = mutableMapOf<String, List<NaverStore>>()
        
        tasks.forEachIndexed { index, (searchKeyword, locationName) ->
            println("\n--- \"$searchKeyword\" ($locationName) 크롤링 시작 ---")
            val stores = crawlAndSave(searchKeyword, locationName, (10..15).random())
            results[locationName] = stores
            
            if (index < tasks.size - 1) {
                val delayTime = (30000L..90000L).random()
                println("\n다음 작업까지 ${delayTime / 1000}초 대기...")
                Thread.sleep(delayTime)
            }
        }
        
        return results
    }

    /**
     * 모든 가게의 주소를 좌표로 변환하여 업데이트합니다.
     */
    @Transactional
    fun updateAllCoordinates() {
        val stores = naverStoreRepository.findAll()
        
        if (stores.isEmpty()) {
            println("✅ DB에 변환할 가게 데이터가 없습니다.")
            return
        }

        println("▶ 총 ${stores.size}개의 가게 주소를 좌표로 변환 및 업데이트합니다...")

        stores.forEachIndexed { index, store ->
            if (store.latitude == null || store.longitude == null) {
                print("\r - [${index + 1}/${stores.size}] '${store.address}' 변환 중...")

                runBlocking {
                    geocodingService.getCoordinates(store.address)
                        .onSuccess { coordinates ->
                            store.latitude = coordinates.latitude
                            store.longitude = coordinates.longitude
                            naverStoreRepository.save(store)
                        }
                        .onFailure { error ->
                            println("\n[변환 실패] 주소: ${store.address}, 원인: ${error.message}")
                        }
                }

                Thread.sleep(100L) // 0.1초 대기
            }
        }

        println("\n\n🎉 좌표 변환 및 업데이트 작업이 완료되었습니다.")
    }

    /**
     * 가게 목록을 데이터베이스에 저장 (upsert 방식)
     */
    private fun saveStores(stores: List<NaverStore>) {
        var insertedCount = 0
        var updatedCount = 0
        var failureCount = 0

        stores.forEachIndexed { index, store ->
            print("\r - [${index + 1}/${stores.size}] '${store.name}' 저장 중...")
            try {
                val existingStore = naverStoreRepository.findByNaverPlaceId(store.naverPlaceId)
                
                if (existingStore == null) {
                    naverStoreRepository.save(store)
                    insertedCount++
                } else {
                    existingStore.name = store.name
                    existingStore.address = store.address
                    existingStore.category = store.category
                    existingStore.visitorReviews = store.visitorReviews
                    existingStore.blogReviews = store.blogReviews
                    existingStore.operatingHours = store.operatingHours
                    existingStore.phoneNum = store.phoneNum
                    existingStore.searchKeyword = store.searchKeyword
                    existingStore.location = store.location
                    naverStoreRepository.save(existingStore)
                    updatedCount++
                }
            } catch (e: Exception) {
                println("\n❌ '${store.name}' 저장 실패: ${e.message}")
                failureCount++
            }
        }
        println("\n- 최종 결과: 신규 ${insertedCount}건, 업데이트 ${updatedCount}건, 실패 ${failureCount}건")
    }

    /**
     * Chrome WebDriver 생성
     */
    private fun createDriver(): WebDriver {
        val options = ChromeOptions().apply {
            val userAgents = listOf(
                "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/109.0.0.0 Safari/537.36",
                "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/109.0.0.0 Safari/537.36"
            )
            addArguments("user-agent=${userAgents.random()}")
            addArguments("window-size=1380,900")
            addArguments("--disable-blink-features=AutomationControlled")
            setExperimentalOption("excludeSwitches", listOf("enable-automation"))
            setExperimentalOption("useAutomationExtension", false)
        }
        return ChromeDriver(options)
    }
}

