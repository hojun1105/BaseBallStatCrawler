package com.demo.service

import com.demo.model.PitcherStat
import com.demo.model.PlayerInfo
import com.demo.model.TeamInfo
import com.demo.repository.PitcherStatRepository
import com.demo.repository.PlayerInfoRepository
import com.demo.repository.TeamInfoRepository
import com.demo.util.Parsers
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
class SavePitcherStatService(
    private val pitcherStatRepository: PitcherStatRepository,
    private val playerInfoRepository: PlayerInfoRepository,
    private val teamInfoRepository: TeamInfoRepository
) {
    fun invoke(stats:List<Pair<List<String>,List<String>>>) {
        val infoEntities = stats.map { (info, _) ->
            PlayerInfo().apply{
                this.name = info[0]
                this.position = info[1]
                this.backNumber = info[2].toInt()
                this.birthDate = Parsers.parseKoreanDate(info[3])
                this.heightWeight = info[4]
                this.salary = Parsers.parseSalary(info[5])
                this.debutYear = Parsers.extractNumber(info[6])
                this.team =getTeamInfoById(info[7].toInt())
            }
        }
        playerInfoRepository.saveAll(infoEntities)

        val pitcherStatEntities = stats.map{(info,stat) ->
            PitcherStat().apply{
                this.player = getPlayerInfoByNameAndTeamName(info[0], stat[0])
                this.date = LocalDate.now()
                this.era = Parsers.parseToBigDecimal(stat[1])
                this.rank = null
                this.games = stat[2].toInt()
                this.wins = stat[5].toInt()
                this.losses = stat[6].toInt()
                this.saves = stat[7].toInt()
                this.holds = stat[8].toInt()
                this.winningPercentage = Parsers.parseToBigDecimal(stat[9])
                this.inningsPitched = Parsers.parseToBigDecimal(stat[12])
                this.hits = stat[13].toInt()
                this.homeRuns = stat[16].toInt()
                this.walks = stat[20].toInt()
                this.hitByPitch = null
                this.strikeOuts = stat[21].toInt()
                this.runs = stat[24].toInt()
                this.earnedRuns = stat[25].toInt()
                this.whip = Parsers.parseToBigDecimal(stat[27])
            }
        }
        pitcherStatRepository.saveAll(pitcherStatEntities)
        println("${pitcherStatEntities.size}명 저장완료")
    }


    fun getTeamInfoById(teamId: Int): TeamInfo {
        return teamInfoRepository.findById(teamId.toLong())
            .orElseThrow { IllegalArgumentException("ID가 $teamId 인 팀이 존재하지 않습니다.") }
    }
    fun getPlayerInfoByNameAndTeamName(name: String, teamName: String): PlayerInfo? {
        val team = teamInfoRepository.findByNameContaining(teamName)
        return playerInfoRepository.findByNameAndTeam(name, team)
    }
}