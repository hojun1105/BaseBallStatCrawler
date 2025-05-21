package com.demo

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.time.MonthDay
import java.time.format.DateTimeFormatter

data class CrawlContainer(
    val homeTeamName: String,
    val homeOps: Double,
    val homeEra: Double,
    val awayTeamName: String,
    val awayEra: Double,
    val awayOps: Double,
    val temperature: Double,
    val humidity: Double,
    val homeTeamResult:Int,
    val awayTeamResult:Int
)

data class TeamStat(
    val teamName:String,
    val ops: Double,
    val era: Double,
    val date: MonthDay
    )

data class MatchResult(
    val homeTeamName: String,
    val awayTeamName: String,
    val homeTeamWin: Boolean,
    val awayTeamWin: Boolean,
    val date: MonthDay,
    val location:String
)

class DeepLearningCrawl {

    fun jsoupGetCustom(url: String): Document {
        return Jsoup.connect(url)
            .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 Chrome/123.0.0.0 Safari/537.36")
            .get()
    }

    fun invoke():List<CrawlContainer>{
        val statUrls = makeStatUrl()
        val documentList = mutableListOf<Document>()
        for (url in statUrls) {
            Thread.sleep((1000..3000).random().toLong()) // 혹은 delay + runBlocking
            val doc = jsoupGetCustom(url)
            documentList.add(doc)
        }

        val stats = documentList.mapNotNull { crawlStat(it) }.flatten()
        val results = documentList.mapNotNull{crawlResult(it)}
        return map(stats,results)
    }

    //우취 고려해야함
    private fun makeStatUrl():List<String>{
        val urlList = mutableListOf<String>()
        for(i in 20250176..20250195){
            val url = "https://statiz.sporki.com/schedule/?m=boxscore&s_no=${i}"
            urlList.add(url)
        }
        return urlList
    }

    private fun makeResultUrl():List<String>{
        val urlList = mutableListOf<String>()
        for(i in 20250196..20250200){
            val url = "https://statiz.sporki.com/schedule/?m=boxscore&s_no=${i}"
            urlList.add(url)
        }
        return urlList
    }


    private fun crawlStat(docs:Document):List<TeamStat>?{
        val resultScore = docs.select("div.game_schedule").select(".score .num").text()
        if(resultScore.equals("경기취소")) return null
        else{
            val teamName = docs.select("div.box_head").text().split(" ").distinct()
                .filter { it.contains("(") }.map{it.replace("(", "").replace(")", "")}
            val awayTeamName= teamName[0]
            val homeTeamName= teamName[1]
            val elements = docs.select("div.table_type03 table").select("tbody tr.total")
            val awayOps = elements[0].select("td").text().split(" ")[17].toDouble()
            val homeOps = elements[1].select("td").text().split(" ")[17].toDouble()
            val awayEra = elements[2].select("td").text().split(" ")[15].toDouble()
            val homeEra = elements[3].select("td").text().split(" ")[15].toDouble()
    //        val awayDefense = elements[4]
    //        val homeDefense = elements[5]
            val formatter = DateTimeFormatter.ofPattern("MM-dd")
            val date = docs.select("div.game_schedule").select(".score .txt").text().split(",").map{it.trim()}[1]
            val monthDay = MonthDay.parse(date,formatter)
            val homeTeam = TeamStat(homeTeamName,homeOps,homeEra, monthDay)
            val awayTeam = TeamStat(awayTeamName,awayOps,awayEra, monthDay)
            val list = mutableListOf<TeamStat>()
            list.add(homeTeam)
            list.add(awayTeam)
            return list
        }
    }

    private fun crawlResult(docs:Document):MatchResult?{
        val result = docs.select("div.game_schedule").select(".score .num").text()
        if(result.equals("경기취소")) return null
        else{
            val resultScore = docs.select("div.game_schedule").select(".score .num span").map{it.text().toInt()}
            val resultData = docs.select("div.game_schedule").select(".score .txt").text().split(",").map{it.trim()}
            val teamName = docs.select("div.box_head").text().split(" ").distinct().filter { it.contains("(") }.map{it.replace("(", "").replace(")", "")}
            val awayTeamName= teamName[0]
            val homeTeamName= teamName[1]
            val homeTeamScore = resultScore[1]
            val awayTeamScore = resultScore[0]
            val homeTeamWin = homeTeamScore>awayTeamScore
            val awayTeamWin = homeTeamScore<awayTeamScore
            val location = resultData[0]
            val formatter = DateTimeFormatter.ofPattern("MM-dd")
            val date = MonthDay.parse(resultData[1], formatter)
            return MatchResult(homeTeamName,awayTeamName,homeTeamWin,awayTeamWin,date,location)
        }
    }

    private fun map(stats : List<TeamStat>, results : List<MatchResult>): List<CrawlContainer>{
        return results.mapNotNull { result ->
            val date = result.date.atYear(2025)
            val previousDate = MonthDay.from(date.minusDays(1))

            val homeStat = stats.find { it.date == previousDate && it.teamName == result.homeTeamName }
            val awayStat = stats.find { it.date == previousDate && it.teamName == result.awayTeamName }

            if (homeStat != null && awayStat != null) {
                CrawlContainer(
                    homeStat.teamName, homeStat.ops, homeStat.era,
                    awayStat.teamName, awayStat.ops, awayStat.era,
                    20.0, 50.0,
                    if (result.homeTeamWin) 1 else 0,
                    if (result.awayTeamWin) 1 else 0
                )
            } else null
        }
    }
}


