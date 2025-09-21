package com.demo.com.demo.service

import com.demo.model.PitcherStat
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
class CrawlService {

    private var hitterStatUrl = "https://www.koreabaseball.com/Record/Player/HitterBasic/Basic1.aspx"
    private var pitcherStatUrl = "https://www.koreabaseball.com/Record/Player/PitcherBasic/Basic1.aspx"

    fun invoke()
    :List<List<String>>
    {
        var urls = crawlAllCombinations(hitterStatUrl)
        return urls.map{crawlHitterStats(it)}
    }
    fun invoke2(){
        crawlHitterStats("https://www.koreabaseball.com/Record/Player/HitterDetail/Basic.aspx?playerId=54529")
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

        // 포지션 값과 라벨
        val positions = mapOf(
            "포수" to "2",
            "내야수" to "3,4,5,6",
            "외야수" to "7,8,9"
        )

        // 팀 코드와 이름
        val teams = mapOf(
            "LG" to "LG", "HH" to "한화", "LT" to "롯데", "SS" to "삼성",
            "SK" to "SSG", "KT" to "KT", "KI" to "키움", "NC" to "NC",
            "OB" to "두산", "WO" to "키움"
        )

        driver.get(url)

        for ((posName, posValue) in positions) {
            val table1 = wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("table.tData01.tt")))
            selectDropdownByScript(driver, "cphContents_cphContents_cphContents_ddlPos_ddlPos", posValue)
            wait.until(ExpectedConditions.stalenessOf(table1))

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
        }
        driver.quit()
        return urls
    }


    fun crawlHitterStats(url:String): List<String> {
        val driver = createDriver()
        driver.get(url)
        val table1Rows = driver.findElements(By.cssSelector("table.tbl.tt tbody tr"))

        // 결과를 저장할 리스트 (행 2개)
        val allRows = mutableListOf<String>()

        for (i in 0..1) { // 0, 1 두 줄만
            val tds = table1Rows[i].findElements(By.tagName("td"))
            val values = tds.map { it.text.trim() }
            allRows.addAll(values)
        }
        return allRows

    }

    fun crawlPitcherStats(): List<PitcherStat> {
        return listOf(/* HitterStat(...) */)
    }
}