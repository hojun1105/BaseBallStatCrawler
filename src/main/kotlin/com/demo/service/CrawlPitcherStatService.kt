package com.demo.com.demo.service

import org.openqa.selenium.By
import org.openqa.selenium.JavascriptExecutor
import org.openqa.selenium.WebDriver
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.chrome.ChromeOptions
import org.openqa.selenium.support.ui.ExpectedConditions
import org.openqa.selenium.support.ui.WebDriverWait
import org.springframework.stereotype.Service
import java.time.Duration

@Service
class CrawlPitcherStatService(
    private val crawlPlayerInfoService: CrawlPlayerInfoService
) {
    private var url = "https://www.koreabaseball.com/Record/Player/PitcherBasic/Basic1.aspx"
    fun invoke(teamName: String)
            :List<Pair<List<String>,List<String>>>
    {
        val teamCode = getTeamCodeFromName(teamName)

        val driver = createDriver()
        val urls = crawlByTeam(teamCode,driver)
        return urls
            .mapNotNull { url ->
            val stat = crawlStats(url,driver)
            val info = crawlPlayerInfoService.invoke(url,driver)
                if(info!=null){
                    info to stat
                }else {
                    null
                }
        }
    }

    private fun createDriver(): WebDriver {
        val options = ChromeOptions()
        options.addArguments("--disable-gpu", "--no-sandbox", "--headless=new")
        return ChromeDriver(options)
    }

    private fun selectDropdownByScript(driver: WebDriver, elementId: String, value: String) {
        val js = driver as JavascriptExecutor
        val script = """
        const ddl = document.getElementById('$elementId');
        ddl.value = '$value';
        ddl.dispatchEvent(new Event('change', { bubbles: true }));
    """.trimIndent()
        js.executeScript(script)
    }

    private fun crawlAllCombinations(url: String):List<String> {

        val driver = createDriver()
        val urls = mutableListOf<String>()
        val wait = WebDriverWait(driver, Duration.ofSeconds(10))

        // 팀 코드와 이름
        val teams = mapOf(
            "LG" to "LG", "HH" to "한화", "LT" to "롯데", "SS" to "삼성",
            "SK" to "SSG", "KT" to "KT", "KI" to "키움", "NC" to "NC",
            "OB" to "두산", "WO" to "키움"
        )

        driver.get(url)

        for ((teamCode, teamName) in teams) {
            val table2 = wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("table.tData01.tt")))
            selectDropdownByScript(driver, "cphContents_cphContents_cphContents_ddlTeam_ddlTeam", teamCode)
            wait.until(ExpectedConditions.stalenessOf(table2))

            wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("table.tData01.tt tbody")))
            val rows = driver.findElements(By.cssSelector("table.tData01.tt tbody tr"))
            for(row in rows) {
                val cols = row.findElements(By.tagName("td"))
                val linkElement = cols[1].findElement(By.tagName("a"))
                val relativeUrl = linkElement.getAttribute("href")
                urls.add(relativeUrl)
            }
        }

        driver.quit()
        return urls
    }
    private fun crawlByTeam(teamCode: String,driver: WebDriver): List<String> {

        val urls = mutableListOf<String>()
        val wait = WebDriverWait(driver, Duration.ofSeconds(10))

        driver.get(url)


            try {
                val table1 = wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("table.tData01.tt")))
                selectDropdownByScript(driver, "cphContents_cphContents_cphContents_ddlTeam_ddlTeam", teamCode)
                wait.until(ExpectedConditions.stalenessOf(table1))

                wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("table.tData01.tt tbody")))
                val rows = driver.findElements(By.cssSelector("table.tData01.tt tbody tr"))

                for (row in rows) {
                    try {
                        val cols = row.findElements(By.tagName("td"))
                        val linkElement = cols[1].findElement(By.tagName("a"))
                        val relativeUrl = linkElement.getAttribute("href")
                        urls.add(relativeUrl)
                    } catch (e: Exception) {
                        println("링크 추출 실패: ${e.message}")
                    }
                }
            } catch (e: Exception) {
                println(" ${e.message}")
            }

        return urls
    }
    private val teamNameToCode = mapOf(
        "엘지" to "LG", "한화" to "HH", "롯데" to "LT", "삼성" to "SS",
        "SSG" to "SK", "KT" to "KT", "키움" to "KI", "엔씨" to "NC",
        "두산" to "OB"
    )

    fun getTeamCodeFromName(name: String): String {
        return teamNameToCode.entries.first {
            name.contains(it.key, ignoreCase = true)
        }.value
    }


    fun crawlStats(url:String,driver:WebDriver): List<String> {
        driver.get(url)
        val table1Rows = driver.findElements(By.cssSelector("table.tbl.tt tbody tr"))

        val allRows = mutableListOf<String>()

        for (i in 0..1) {
            val tds = table1Rows[i].findElements(By.tagName("td"))
            val values = tds.map { it.text.trim() }
            allRows.addAll(values)
        }
        return allRows
    }
}