package com.demo

import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.chrome.ChromeOptions
import org.openqa.selenium.support.ui.ExpectedConditions
import org.openqa.selenium.support.ui.WebDriverWait
import java.sql.Connection
import java.sql.DriverManager
import java.time.Duration
import java.time.LocalDate

object BaseballArchive {

    private const val DB_URL = "jdbc:postgresql://localhost:5432/postgres"
    private const val DB_USER = "postgres"
    private const val DB_PASSWORD = "1234"

    private val teamMap = mapOf(
        "KIA" to 1, "LG" to 2, "삼성" to 3, "SSG" to 4, "한화" to 5,
        "롯데" to 6, "두산" to 7, "키움" to 8, "KT" to 9, "NC" to 10
    )

    private fun createDriver(): WebDriver {
        val options = ChromeOptions()
        options.addArguments("--disable-gpu")
        options.addArguments("--no-sandbox")
        options.addArguments("--headless=new")
        return ChromeDriver(options)
    }

    private fun createConnection(): Connection {
        Class.forName("org.postgresql.Driver")
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)
    }

    private fun safeToInt(text: String): Int = text.replace(",", "").toIntOrNull() ?: 0
    private fun safeToDouble(text: String): Double = text.replace(",", "").toDoubleOrNull() ?: 0.0

    fun crawlAndSavePlayerInfo() {
        val conn = createConnection()
        val driver = createDriver()

        try {
            driver.get("https://www.koreabaseball.com/Record/Player/HitterBasic/Basic1.aspx")
            val wait = WebDriverWait(driver, Duration.ofSeconds(10))
            wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("table.tData01.tt")))

            val table = driver.findElement(By.cssSelector("table.tData01.tt"))
            val rows = table.findElement(By.tagName("tbody")).findElements(By.tagName("tr"))

            val insertSql = """
                INSERT INTO player_info (name, team_id, position)
                VALUES (?, ?, ?)
                ON CONFLICT (name, team_id) DO UPDATE SET position = EXCLUDED.position
            """.trimIndent()
            val pstmt = conn.prepareStatement(insertSql)

            for (row in rows) {
                val cols = row.findElements(By.tagName("td"))
                if (cols.size < 4) continue
                val name = cols[1].text.trim()
                val teamName = cols[2].text.trim()
                val position = cols[3].text.trim()
                val teamId = teamMap[teamName] ?: continue

                pstmt.setString(1, name)
                pstmt.setInt(2, teamId)
                pstmt.setString(3, position)
                pstmt.executeUpdate()
            }

            pstmt.close()
            println("✅ 선수 정보가 DB에 저장 또는 업데이트 되었습니다.")
        } finally {
            driver.quit()
            conn.close()
        }
    }

    fun crawlPitcherStatsAndSave() {
        val conn = createConnection()
        val driver = createDriver()
        val date = LocalDate.now()

        try {
            val pitcherMap = mutableMapOf<String, MutableList<String>>()

            fun extractStatsFromPage(url: String, isSecond: Boolean = false) {
                driver.get(url)
                val wait = WebDriverWait(driver, Duration.ofSeconds(10))
                wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("table.tData01.tt")))

                val table = driver.findElement(By.cssSelector("table.tData01.tt"))
                val rows = table.findElement(By.tagName("tbody")).findElements(By.tagName("tr"))

                for (row in rows) {
                    val cols = row.findElements(By.tagName("td"))
                    if (cols.size < 4) continue

                    val name = cols[1].text.trim()
                    val team = cols[2].text.trim()
                    val key = "$name|$team"
                    val stats = cols.drop(3).map { it.text.trim() }

                    if (!isSecond) {
                        pitcherMap[key] = stats.toMutableList()
                    } else {
                        if (pitcherMap[key] == null) {
                            println("❌ 병합 실패: $key → Basic1에 없음")
                            continue
                        }
                        val statsWithoutERA = stats.drop(1)
                        pitcherMap[key]?.addAll(statsWithoutERA)
                    }
                }
            }

            extractStatsFromPage("https://www.koreabaseball.com/Record/Player/PitcherBasic/Basic1.aspx", false)
            extractStatsFromPage("https://www.koreabaseball.com/Record/Player/PitcherBasic/Basic2.aspx", true)

            val playerIdStmt = conn.prepareStatement("SELECT id FROM player_info WHERE name = ? AND team_id = ?")
            val insertSql = """
                INSERT INTO pitcher_stat (
                    player_id, date,
                    earned_run_average, games, wins, loses, save, hold, winning_percentage,
                    innings_pitched, hits, home_run, base_on_balls, hit_by_pitch, strike_out,
                    runs, earned_run, walks_plus_hits_divided_by_innings_pitched,
                    complete_game, shutouts, quality_start, blown_save, total_batters_faced,
                    number_of_pitches, average, double_base, triple_base,
                    sacrifice_hit, sacrifice_fly, intentional_based_on_balls, wild_pitch, balk
                ) VALUES (
                    ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?
                )
                ON CONFLICT (player_id, date) DO UPDATE SET
                    earned_run_average = EXCLUDED.earned_run_average,
                    games = EXCLUDED.games,
                    wins = EXCLUDED.wins,
                    loses = EXCLUDED.loses,
                    save = EXCLUDED.save,
                    hold = EXCLUDED.hold,
                    winning_percentage = EXCLUDED.winning_percentage,
                    innings_pitched = EXCLUDED.innings_pitched,
                    hits = EXCLUDED.hits,
                    home_run = EXCLUDED.home_run,
                    base_on_balls = EXCLUDED.base_on_balls,
                    hit_by_pitch = EXCLUDED.hit_by_pitch,
                    strike_out = EXCLUDED.strike_out,
                    runs = EXCLUDED.runs,
                    earned_run = EXCLUDED.earned_run,
                    walks_plus_hits_divided_by_innings_pitched = EXCLUDED.walks_plus_hits_divided_by_innings_pitched,
                    complete_game = EXCLUDED.complete_game,
                    shutouts = EXCLUDED.shutouts,
                    quality_start = EXCLUDED.quality_start,
                    blown_save = EXCLUDED.blown_save,
                    total_batters_faced = EXCLUDED.total_batters_faced,
                    number_of_pitches = EXCLUDED.number_of_pitches,
                    average = EXCLUDED.average,
                    double_base = EXCLUDED.double_base,
                    triple_base = EXCLUDED.triple_base,
                    sacrifice_hit = EXCLUDED.sacrifice_hit,
                    sacrifice_fly = EXCLUDED.sacrifice_fly,
                    intentional_based_on_balls = EXCLUDED.intentional_based_on_balls,
                    wild_pitch = EXCLUDED.wild_pitch,
                    balk = EXCLUDED.balk
            """.trimIndent()

            val pstmt = conn.prepareStatement(insertSql)
            var inserted = 0

            for ((key, stats) in pitcherMap) {
                val (name, team) = key.split("|")
                val teamId = teamMap[team] ?: continue

                if (stats.size != 30) {
                    println("⚠️ 데이터 부족: $key → ${stats.size}개 항목")
                    continue
                }

                playerIdStmt.setString(1, name)
                playerIdStmt.setInt(2, teamId)
                val rs = playerIdStmt.executeQuery()
                if (!rs.next()) {
                    println("❌ player_id 찾기 실패: name='$name' / team='$team'")
                    continue
                }
                val playerId = rs.getInt("id")
                rs.close()

                pstmt.setInt(1, playerId)
                pstmt.setDate(2, java.sql.Date.valueOf(date))

                for (i in 0 until 30) {
                    val idx = i + 3
                    val value = stats[i]
                    if (i in listOf(0, 7, 16, 23)) {
                        pstmt.setDouble(idx, safeToDouble(value))
                    } else {
                        pstmt.setInt(idx, safeToInt(value))
                    }
                }

                pstmt.executeUpdate()
                inserted++
            }

            println("✅ $inserted 개의 투수 기록이 DB에 저장 또는 업데이트 되었습니다.")
            pstmt.close()
            playerIdStmt.close()
        } finally {
            driver.quit()
            conn.close()
        }
    }

    fun crawlHitterStatsAndSave() {
        val conn = createConnection()
        val driver = createDriver()
        val date = LocalDate.now()

        try {
            val hitterMap = mutableMapOf<String, MutableList<String>>()

            fun extractStatsFromPage(url: String, isSecond: Boolean = false) {
                driver.get(url)
                val wait = WebDriverWait(driver, Duration.ofSeconds(10))
                wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("table.tData01.tt")))

                val table = driver.findElement(By.cssSelector("table.tData01.tt"))
                val rows = table.findElement(By.tagName("tbody")).findElements(By.tagName("tr"))

                for (row in rows) {
                    val cols = row.findElements(By.tagName("td"))
                    if (cols.size < 4) continue

                    val name = cols[1].text.trim()
                    val team = cols[2].text.trim()
                    val key = "$name|$team"
                    val stats = cols.drop(3).map { it.text.trim() }

                    if (!isSecond) {
                        hitterMap[key] = stats.toMutableList()
                    } else {
                        if (hitterMap[key] == null) {
                            println("❌ 병합 실패: $key → Basic1에 없음")
                            continue
                        }
                        val statsWithoutAVG = stats.drop(1)
                        hitterMap[key]?.addAll(statsWithoutAVG)
                    }
                }
            }

            extractStatsFromPage("https://www.koreabaseball.com/Record/Player/HitterBasic/Basic1.aspx", false)
            extractStatsFromPage("https://www.koreabaseball.com/Record/Player/HitterBasic/Basic2.aspx", true)

            val playerIdStmt = conn.prepareStatement("SELECT id FROM player_info WHERE name = ? AND team_id = ?")
            val insertSql = """
                INSERT INTO hitter_stat (
                    player_id, date,
                    average, games, plate_appearance, at_bat, runs, hits, double_base, triple_base,
                    home_run, total_base, runs_batted_in, sacrifice_hit, sacrifice_fly, based_on_balls,
                    intentional_based_on_balls, hit_by_pitch, strike_out, grounded_into_double_play,
                    slugging_percentage, base_percentage, on_base_plus_slugging, multi_hit,
                    runners_in_scoring_position, substitute_hitter_batting_average
                ) VALUES (
                    ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?
                )
                ON CONFLICT (player_id, date) DO UPDATE SET
                    average = EXCLUDED.average,
                    games = EXCLUDED.games,
                    plate_appearance = EXCLUDED.plate_appearance,
                    at_bat = EXCLUDED.at_bat,
                    runs = EXCLUDED.runs,
                    hits = EXCLUDED.hits,
                    double_base = EXCLUDED.double_base,
                    triple_base = EXCLUDED.triple_base,
                    home_run = EXCLUDED.home_run,
                    total_base = EXCLUDED.total_base,
                    runs_batted_in = EXCLUDED.runs_batted_in,
                    sacrifice_hit = EXCLUDED.sacrifice_hit,
                    sacrifice_fly = EXCLUDED.sacrifice_fly,
                    based_on_balls = EXCLUDED.based_on_balls,
                    intentional_based_on_balls = EXCLUDED.intentional_based_on_balls,
                    hit_by_pitch = EXCLUDED.hit_by_pitch,
                    strike_out = EXCLUDED.strike_out,
                    grounded_into_double_play = EXCLUDED.grounded_into_double_play,
                    slugging_percentage = EXCLUDED.slugging_percentage,
                    base_percentage = EXCLUDED.base_percentage,
                    on_base_plus_slugging = EXCLUDED.on_base_plus_slugging,
                    multi_hit = EXCLUDED.multi_hit,
                    runners_in_scoring_position = EXCLUDED.runners_in_scoring_position,
                    substitute_hitter_batting_average = EXCLUDED.substitute_hitter_batting_average
            """.trimIndent()

            val pstmt = conn.prepareStatement(insertSql)
            var inserted = 0

            for ((key, stats) in hitterMap) {
                val (name, team) = key.split("|")
                val teamId = teamMap[team] ?: continue

                if (stats.size != 23) {
                    println("⚠️ 데이터 부족: $key → ${stats.size}개 항목")
                    continue
                }

                playerIdStmt.setString(1, name)
                playerIdStmt.setInt(2, teamId)
                val rs = playerIdStmt.executeQuery()
                if (!rs.next()) {
                    println("❌ player_id 찾기 실패: name='$name' / team='$team'")
                    continue
                }
                val playerId = rs.getInt("id")
                rs.close()

                pstmt.setInt(1, playerId)
                pstmt.setDate(2, java.sql.Date.valueOf(date))

                for (i in 0 until 23) {
                    val idx = i + 3
                    val value = stats[i]
                    if (i in listOf(0, 20, 21, 22)) {
                        pstmt.setDouble(idx, safeToDouble(value))
                    } else {
                        pstmt.setInt(idx, safeToInt(value))
                    }
                }

                pstmt.executeUpdate()
                inserted++
            }

            println("✅ $inserted 개의 타자 기록이 DB에 저장 또는 업데이트 되었습니다.")
            pstmt.close()
            playerIdStmt.close()
        } finally {
            driver.quit()
            conn.close()
        }
    }

    fun crawlAll() {
        crawlAndSavePlayerInfo()
        crawlPitcherStatsAndSave()
        crawlHitterStatsAndSave()
    }
}
