package com.demo.service

import com.demo.config.CrawlerConfig
import com.demo.model.NaverStore
import com.demo.util.IframeSwitcher
import org.openqa.selenium.*
import org.openqa.selenium.support.ui.ExpectedConditions
import org.openqa.selenium.support.ui.WebDriverWait
import org.springframework.stereotype.Service
import java.time.Duration
import kotlin.math.min

/**
 * 네이버 지도 크롤링 서비스
 */
@Service
class NaverMapCrawlerService(
    private val config: CrawlerConfig
) {
    private val placeIdRegex = Regex("""place/(\d+)""")

    /**
     * 주어진 검색어로 가게 정보를 스크래핑합니다.
     * @param driver WebDriver 인스턴스
     * @param searchKeyword 검색어
     * @param locationName 위치 이름
     * @param maxStoresPerPage 최대 수집할 가게 수
     * @return 수집된 NaverStore 객체의 리스트
     */
    fun scrape(driver: WebDriver, searchKeyword: String, locationName: String, maxStoresPerPage: Int): List<NaverStore> {
        val collectedStores = mutableListOf<NaverStore>()
        val switcher = IframeSwitcher(driver, config)

        driver.get("https://map.naver.com/p/search/$searchKeyword")
        waitForStoreListToLoad(driver, switcher)
        scrollDown(driver, maxStoresPerPage, switcher)

        val combinedSelector = "${config.storeListItem}, ${config.storeListItemFallback}"
        val totalStoresOnPage = driver.findElements(By.cssSelector(combinedSelector)).size
        val storesToScrape = min(totalStoresOnPage, maxStoresPerPage)

        println("페이지 내 총 ${totalStoresOnPage}개의 가게 로딩됨. ${storesToScrape}개만 크롤링합니다.")

        for (i in 0 until storesToScrape) {
            try {
                val store = extractStoreInfo(driver, switcher, i, searchKeyword, locationName)
                if (store != null) {
                    collectedStores.add(store)
                    println(" -> [${i + 1}/${storesToScrape}] 상세 정보 추출 완료: ${store.name} (ID: ${store.naverPlaceId})")
                }
            } catch (e: StaleElementReferenceException) {
                println("StaleElementReferenceException 발생. 다음 가게로 넘어갑니다.")
            } catch (e: Exception) {
                println("가게 상세 정보 크롤링 중 오류: ${e.message}")
            }
        }
        return collectedStores
    }

    private fun waitForStoreListToLoad(driver: WebDriver, switcher: IframeSwitcher) {
        val wait = WebDriverWait(driver, Duration.ofSeconds(10))
        switcher.switchToSearchList()
        println("-> 가게 목록이 '보일 때까지' 대기합니다...")
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(config.storeListItem)))
        } catch (e: TimeoutException) {
            println("-> 기본 선택자를 찾지 못했습니다. 대체 선택자로 다시 시도합니다.")
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(config.storeListItemFallback)))
        }
        println("-> 가게 목록 로딩 완료.")
    }

    private fun scrollDown(driver: WebDriver, requiredItemCount: Int, switcher: IframeSwitcher) {
        val js = driver as JavascriptExecutor
        val combinedSelector = "${config.storeListItem}, ${config.storeListItemFallback}"
        try {
            val scrollableElement = driver.findElement(By.cssSelector(config.scrollableElement))
            var lastHeight = js.executeScript("return arguments[0].scrollHeight", scrollableElement) as Long

            while (true) {
                val currentItemCount = driver.findElements(By.cssSelector(combinedSelector)).size
                if (currentItemCount >= requiredItemCount) {
                    println("필요한 가게 수(${requiredItemCount}개)만큼 로딩되어 스크롤을 중단합니다.")
                    break
                }

                js.executeScript("arguments[0].scrollTop = arguments[0].scrollHeight", scrollableElement)
                Thread.sleep((800L..1500L).random())
                val newHeight = js.executeScript("return arguments[0].scrollHeight", scrollableElement) as Long
                if (newHeight == lastHeight) {
                    println("페이지 끝까지 스크롤했습니다.")
                    break
                }
                lastHeight = newHeight
            }
        } catch (e: Exception) {
            // 스크롤 요소가 없어도 그냥 진행
        }
    }

    private fun extractStoreInfo(
        driver: WebDriver,
        switcher: IframeSwitcher,
        index: Int,
        searchKeyword: String,
        locationName: String
    ): NaverStore? {
        switcher.switchToSearchList()
        val combinedSelector = "${config.storeListItem}, ${config.storeListItemFallback}"
        val currentStoreElement = driver.findElements(By.cssSelector(combinedSelector))[index]

        val storeNameElement = try {
            currentStoreElement.findElement(By.cssSelector(config.storeNameInList))
        } catch (e: NoSuchElementException) {
            currentStoreElement.findElement(By.cssSelector(config.storeNameInListFallback))
        }
        val name = storeNameElement.text
        if (name.isBlank()) return null

        storeNameElement.click()
        Thread.sleep((2000L..4000L).random())

        val currentUrl = driver.currentUrl
        val placeIdMatch = placeIdRegex.find(currentUrl)
        val naverPlaceId = placeIdMatch?.groupValues?.get(1) ?: run {
            println("-> '${name}'의 고유 ID를 URL에서 찾을 수 없습니다. 건너뜁니다.")
            return null
        }

        switcher.switchToDetailView()
        return extractStoreDetails(driver, name, naverPlaceId, searchKeyword, locationName)
    }

    private fun extractStoreDetails(
        driver: WebDriver,
        name: String,
        naverPlaceId: String,
        searchKeyword: String,
        locationName: String
    ): NaverStore {
        val detailWait = WebDriverWait(driver, Duration.ofSeconds(5))
        detailWait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(config.detailViewContainer)))

        val category = driver.safeFindText(By.cssSelector(config.categoryInDetail)) ?: "카테고리 없음"
        val address = driver.safeFindText(By.cssSelector(config.addressInDetail)) ?: "주소 없음"
        val phoneNum = driver.safeFindText(By.cssSelector(config.phoneInDetail)) ?: "전화번호 없음"

        val reviews = driver.findElements(By.cssSelector(config.reviewCountLink))
        val visitorReviews = reviews.find { it.text.contains("방문자 리뷰") }?.text?.replace(Regex("[^0-9]"), "") ?: "0"
        val blogReviews = reviews.find { it.text.contains("블로그 리뷰") }?.text?.replace(Regex("[^0-9]"), "") ?: "0"

        var operatingHours = "정보 없음"
        try {
            driver.findElements(By.cssSelector(config.operatingHoursMoreButton)).firstOrNull()?.click()
            Thread.sleep(300)
            val hoursElements = driver.findElements(By.cssSelector(config.operatingHoursDayItem))
            if (hoursElements.isNotEmpty()) {
                operatingHours = hoursElements.joinToString(" | ") { el ->
                    val day = el.safeFindText(By.cssSelector(config.operatingHoursDayOfWeek)) ?: ""
                    val time = el.safeFindText(By.cssSelector(config.operatingHoursTime)) ?: ""
                    "$day $time".trim()
                }
            }
        } catch (e: Exception) { /* 영업시간 정보가 없는 경우 무시 */ }

        return NaverStore().apply {
            this.naverPlaceId = naverPlaceId
            this.name = name
            this.category = category
            this.visitorReviews = visitorReviews
            this.blogReviews = blogReviews
            this.operatingHours = operatingHours
            this.address = address
            this.phoneNum = phoneNum
            this.searchKeyword = searchKeyword
            this.location = locationName
        }
    }

    private fun WebDriver.safeFindText(by: By): String? = try {
        findElement(by).text
    } catch (e: NoSuchElementException) {
        null
    }

    private fun WebElement.safeFindText(by: By): String? = try {
        findElement(by).text
    } catch (e: NoSuchElementException) {
        null
    }
}

