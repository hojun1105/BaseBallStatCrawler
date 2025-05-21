package com.demo

import org.jsoup.Jsoup
import java.io.File
import java.io.FileWriter

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
//    val list = mutableListOf<CrawlContainer>()
//    val deepLearningCrawl =  DeepLearning()
//    val result = deepLearningCrawl.invoke()
//    result.forEach {list.add(it)}
//    makeCsvFile2(list)
}
