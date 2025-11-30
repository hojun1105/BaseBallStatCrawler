package com.demo.util

import com.demo.config.CrawlerConfig
import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import org.openqa.selenium.support.ui.ExpectedConditions
import org.openqa.selenium.support.ui.WebDriverWait
import java.time.Duration

/**
 * 네이버 지도의 복잡한 iframe 간 전환을 쉽게 처리하는 헬퍼 클래스
 */
class IframeSwitcher(
    private val driver: WebDriver,
    private val config: CrawlerConfig
) {
    private val wait = WebDriverWait(driver, Duration.ofSeconds(20))

    fun switchToSearchList() {
        driver.switchTo().defaultContent()
        wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(By.id(config.iframeSearch)))
    }

    fun switchToDetailView() {
        driver.switchTo().defaultContent()
        wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(By.id(config.iframeDetail)))
    }
}


