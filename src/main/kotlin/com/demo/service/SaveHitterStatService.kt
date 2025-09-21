package com.demo.com.demo.service

import com.demo.com.demo.repository.HitterStatRepository
import com.demo.com.demo.repository.PlayerInfoRepository
import com.demo.com.demo.util.Parsers
import com.demo.model.HitterStat
import com.demo.model.PlayerInfo
import com.demo.model.TeamInfo
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
class SaveHitterStatService(
    private val hitterStatRepository: HitterStatRepository,
    private val teamInfoRepository: com.demo.com.demo.repository.TeamInfoRepository,
    private val playerInfoRepository: PlayerInfoRepository
) {
    fun invoke(stats:List<Pair<List<String>,List<String>>>) {
        stats.mapNotNull { (info, stat) ->
            runCatching {
                val player = PlayerInfo().apply {
                    name = info[0]
                    position = info[1]
                    backNumber = info[2].toInt()
                    birthDate = Parsers.parseKoreanDate(info[3])
                    heightWeight = info[4]
                    salary = Parsers.parseSalary(info[5])
                    debutYear = Parsers.extractNumber(info[6])
                    team = getTeamInfoById(info[7].toInt())
                }

                val hitterStat = HitterStat().apply {
                    this.player = player
                    this.date = LocalDate.now()
                    this.average = Parsers.parsePercentage(stat[1])
                    this.games = stat[2].toInt()
                    this.plateAppearances = stat[3].toInt()
                    this.atBat = stat[4].toInt()
                    this.runs = stat[5].toInt()
                    this.hits = stat[6].toInt()
                    this.doubleBase = stat[7].toInt()
                    this.tripleBase = stat[8].toInt()
                    this.homeRun = stat[9].toInt()
                    this.totalBase = stat[10].toInt()
                    this.runsBattedIn = stat[11].toInt()
                    this.stolenBase = stat[12].toInt()
                    this.caughtStealing = stat[13].toInt()
                    this.sacrificeHit = stat[14].toInt()
                    this.sacrificeFly = stat[15].toInt()
                    this.basedOnBalls = stat[16].toInt()
                    this.intentionalBasedOnBalls = stat[17].toInt()
                    this.hitByPitch = stat[18].toInt()
                    this.strikeOut = stat[19].toInt()
                    this.groundedIntoDoublePlay = stat[20].toInt()
                    this.sluggingPercentage = Parsers.parsePercentage(stat[21])
                    this.onBasePercentage = Parsers.parsePercentage(stat[22])
                    this.error = stat[23].toInt()
                    this.stolenBasePercentage = Parsers.parsePercentage(stat[24])
                    this.multiHit = stat[25].toInt()
                    this.onBasePlusSlugging = Parsers.parsePercentage(stat[26])
                    this.runnersInScoringPosition = Parsers.parsePercentage(stat[27])
                    this.substituteHitterBattingAverage = Parsers.parsePercentage(stat[28])
                }

                playerInfoRepository.save(player)
                hitterStatRepository.save(hitterStat)
            }
        }


        println(" ${stats.size}명 저장 완료")
    }

//    fun parseKoreanDate(dateString: String?): LocalDate? {
//        return dateString
//            ?.takeIf { it.isNotBlank() }
//            ?.let {
//                val formatter = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일")
//                LocalDate.parse(it, formatter)
//            }
//    }
//
//    fun parseSalary(salaryString: String?): Long? {
//        return salaryString
//            ?.replace("[^\\d]".toRegex(), "")  // 숫자만 남기고 나머지 제거
//            ?.takeIf { it.isNotBlank() }
//            ?.toLongOrNull()
//    }
//
//    fun extractNumber(text: String?): Int? {
//        return text
//            ?.replace("[^\\d]".toRegex(), "") // 숫자만 남기고 제거
//            ?.takeIf { it.isNotBlank() }
//            ?.toIntOrNull()
//    }

    fun getTeamInfoById(teamId: Int): TeamInfo {
        return teamInfoRepository.findById(teamId.toLong())
            .orElseThrow { IllegalArgumentException("ID가 $teamId 인 팀이 존재하지 않습니다.") }
    }

//    fun parsePercentage(value: String): Double? {
//        return when {
//            value.trim() == "-" -> null
//            value.contains("%") -> value.replace("%", "").trim().toDoubleOrNull()?.div(100)
//            else -> value.trim().toDoubleOrNull()
//        }
//    }


    fun getPlayerInfoByNameAndTeamName(name: String, teamName: String): PlayerInfo? {
        val team = teamInfoRepository.findByNameContaining(teamName)
        return playerInfoRepository.findByNameAndTeam(name, team)
    }
}