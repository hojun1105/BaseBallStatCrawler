package com.demo.com.demo.service

import com.demo.com.demo.repository.PitcherStatRepository
import com.demo.com.demo.repository.PlayerInfoRepository
import com.demo.com.demo.repository.TeamInfoRepository
import com.demo.com.demo.util.Parsers
import com.demo.model.PitcherStat
import com.demo.model.PlayerInfo
import com.demo.model.TeamInfo
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.time.LocalDate

@Service
class SavePitcherStatService(
    private val pitcherStatRepository: PitcherStatRepository,
    private val playerInfoRepository: PlayerInfoRepository,
    private val teamInfoRepository: TeamInfoRepository
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

                var pitcherStat = PitcherStat().apply {
                    this.player = player
                    this.date = LocalDate.now()
                    this.era = stat[1].toBigDecimalOrNullIfDash()
                    this.rank = null
                    this.games = stat[2].toIntOrNullIfDash()
                    this.wins = stat[5].toIntOrNullIfDash()
                    this.losses = stat[6].toIntOrNullIfDash()
                    this.saves = stat[7].toIntOrNullIfDash()
                    this.holds = stat[8].toIntOrNullIfDash()
                    this.winningPercentage = stat[9].toBigDecimalOrNullIfDash()
                    this.inningsPitched = stat[12].toBigDecimalOrNullIfDash()
                    this.hits = stat[13].toIntOrNullIfDash()
                    this.homeRuns = stat[16].toIntOrNullIfDash()
                    this.walks = stat[20].toIntOrNullIfDash()
                    this.hitByPitch = null
                    this.strikeOuts = stat[21].toIntOrNullIfDash()
                    this.runs = stat[24].toIntOrNullIfDash()
                    this.earnedRuns = stat[25].toIntOrNullIfDash()
                    this.whip = stat[27].toBigDecimalOrNullIfDash()
                }

                playerInfoRepository.save(player)
                pitcherStatRepository.save(pitcherStat)
            }
        }
        println("${stats.size}명 저장 완료")
    }


    fun getTeamInfoById(teamId: Int): TeamInfo {
        return teamInfoRepository.findById(teamId.toLong())
            .orElseThrow { IllegalArgumentException("ID가 $teamId 인 팀이 존재하지 않습니다.") }
    }
    fun getPlayerInfoByNameAndTeamName(name: String, teamName: String): PlayerInfo? {
        val team = teamInfoRepository.findByNameContaining(teamName)
        return playerInfoRepository.findByNameAndTeam(name, team)
    }

    fun String.toIntOrNullIfDash(): Int? =
        if (this == "-") null else this.toIntOrNull()

    fun String.toBigDecimalOrNullIfDash(): BigDecimal? =
        if (this == "-") null else this.toBigDecimalOrNull()
}