package com.demo

import com.demo.service.CrawlHitterStatService
import com.demo.service.CrawlPitcherStatService
import com.demo.service.CrawlPlayerInfoService
import org.jsoup.Jsoup
import java.io.File
import java.io.FileWriter
import java.time.LocalDate


data class Statiz(
    val team_name: String,
    var run_scored: String,
    var run_allowed: String
)

data class KBO(
    val team_name: String,
    var games: String,
    var win : String,
    var loss: String
)

data class Target(
    val team_name: String,
    var run_scored: String,
    var run_allowed: String,
    var games: String,
    var win : String,
    var loss: String
)

fun crawl_statiz(): MutableList<Target> {
    val siteBatting = "https://statiz.sporki.com/stats/?m=team&m2=batting"
    val sitePitching = "https://statiz.sporki.com/stats/?m=team&m2=pitching"

    val url_list = mutableListOf<String>(siteBatting, sitePitching)
    val list = mutableListOf<Target>()

    for(url in url_list) {
        val docs= Jsoup.connect(url).get()

        val elements = docs.select("div.table_type01 table")
        val secondTable = elements[1]
        val tbody = secondTable.select("tbody")
        val rows = tbody.select("tr")
        if(url.contains(siteBatting)){
            for(row in rows){
                val cells = row.select("td").map{it.text().trim()}
                val pair = Target(cells[1],cells[10],"","","","")
                list.add(pair)
            }
        }
        else{
            for(row in rows){
                val cells = row.select("td").map{it.text().trim()}
                val target = list.find{it.team_name==cells[1] }
                target!!.run_allowed = cells[16]
                target!!.games = cells[4]
                target!!.win = cells[10]
                target!!.loss = cells[11]
            }
        }
    }
    list.forEach { println(it) }
    return list
}



fun makeCsvFile(target: MutableList<Target>) {
    val list = crawl_statiz()
    val file = File("baseball_data.csv")


    // FileWriter(file, append = false) → 덮어쓰기
    FileWriter(file, false).use { writer ->
        for (row in target) {
            val line = listOf(
                row.team_name ?: "",
                row.run_scored?.toString() ?: "",
                row.run_allowed?.toString() ?: "",
                row.games?.toString() ?: "",
                row.win?.toString() ?: "",
                row.loss?.toString() ?: ""
            ).joinToString(",") + "\n"
            writer.write(line)
        }
        println("CSV 파일 생성 완료: ${file.absolutePath}")
    }

}

fun makeCsvFile2(target: MutableList<CrawlContainer>) {
    val list = crawl_statiz()
    val file = File("deepLearning.csv")

    FileWriter(file, false).use { writer ->
        // ✅ 헤더 추가
        val header = listOf(
            "homeTeamName",
            "awayTeamName",
            "homeOps",
            "homeEra",
            "awayOps",
            "awayEra",
            "temperature",
            "humidity"
        ).joinToString(",") + "\n"
        writer.write(header)

        // ✅ 데이터 작성
        for (row in target) {
            val line = listOf(
                row.homeTeamName ?: "",
                row.awayTeamName?.toString() ?: "",
                row.homeOps?.toString() ?: "",
                row.homeEra?.toString() ?: "",
                row.awayOps?.toString() ?: "",
                row.awayEra?.toString() ?: "",
                row.temperature?.toString() ?: "",
                row.humidity?.toString() ?: ""
            ).joinToString(",") + "\n"
            writer.write(line)
        }

        println("CSV 파일 생성 완료: ${file.absolutePath}")
    }
}



fun main() {

    val playerInfoService = CrawlPlayerInfoService()
    val pitcherStatService = CrawlPitcherStatService(playerInfoService)

    // 테스트할 팀 이름을 지정합니다.
    val teamName = "엘지"

    try {
        println(">> '$teamName' 팀의 투수 데이터를 가져옵니다...")

        // 투수 서비스의 invoke 함수를 호출합니다.
        val result = pitcherStatService.invoke(teamName)

        println("총 투수 수: ${result.size}")
        for ((info, stats) in result) {
            println("선수 정보: $info")
            println("선수 스탯: $stats")
            println("=====================================")
        }
    } catch (e: Exception) {
        println("크롤링 중 오류 발생: ${e.message}")
        e.printStackTrace()
    }
}

