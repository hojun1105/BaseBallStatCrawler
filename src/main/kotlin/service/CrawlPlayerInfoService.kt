package com.demo.service

import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.chrome.ChromeOptions
import org.springframework.stereotype.Service

@Service
class CrawlPlayerInfoService {

    fun invoke(url:String): List<String> {
        var driver = createDriver()
        driver.get(url)
        val teamName = driver.findElement(By.id("h4Team")).text.trim()
        val teamId = teamNameToId[teamName] ?: throw IllegalArgumentException("알 수 없는 팀 이름: $teamName")
        val result = mutableListOf<String>()
        val spanIds = listOf(
            "cphContents_cphContents_cphContents_playerProfile_lblName",
            "cphContents_cphContents_cphContents_playerProfile_lblPosition",
            "cphContents_cphContents_cphContents_playerProfile_lblBackNo",
            "cphContents_cphContents_cphContents_playerProfile_lblBirthday",
            "cphContents_cphContents_cphContents_playerProfile_lblHeightWeight",
            "cphContents_cphContents_cphContents_playerProfile_lblSalary",
            "cphContents_cphContents_cphContents_playerProfile_lblJoinInfo",
        )
        for (id in spanIds) {
            try {
                val text = driver.findElement(By.id(id)).text.trim()
                result.add(text)
            } catch (e: Exception) {
                result.add("")  // 해당 id 없으면 빈값 추가
            }
        }
        result.add(teamId)
        driver.quit()
        return result
    }

    private fun createDriver(): WebDriver {
        val options = ChromeOptions()
        options.addArguments("--disable-gpu", "--no-sandbox", "--headless=new")
        return ChromeDriver(options)
    }

    val teamNameToId = mapOf(
        "LG 트윈스" to "1",
        "두산 베어스" to "2",
        "SSG 랜더스" to "3",
        "NC 다이노스" to "4",
        "KT 위즈" to "5",
        "KIA 타이거즈" to "6",
        "삼성 라이온즈" to "7",
        "롯데 자이언츠" to "8",
        "한화 이글스" to "9",
        "키움 히어로즈" to "10"
    )
}