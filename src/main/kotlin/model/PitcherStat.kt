package com.demo.model

import jakarta.persistence.*
import java.math.BigDecimal
import java.time.LocalDate

@Entity
@Table(
    name = "pitcher_stat_1",
    schema = "kbo",
    uniqueConstraints = [UniqueConstraint(columnNames = ["player_id", "\"date\""])]
)
class PitcherStat(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)   // BIGSERIAL
    var id: Long? = null,

    // FK: player_info(id)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "player_id", nullable = false)
    var player: PlayerInfo? = null,

    @Column(name = "\"date\"", nullable = false)          // 따옴표 컬럼
    var date: LocalDate? = null,

    @Column(name = "rank")
    var rank: Int? = null,

    @Column(name = "era")
    var era: BigDecimal? = null,                          // NUMERIC(5,2)

    @Column(name = "games")
    var games: Int? = null,

    @Column(name = "wins")
    var wins: Int? = null,

    @Column(name = "losses")
    var losses: Int? = null,

    @Column(name = "saves")
    var saves: Int? = null,

    @Column(name = "holds")
    var holds: Int? = null,

    @Column(name = "winning_percentage")
    var winningPercentage: BigDecimal? = null,            // NUMERIC(5,3)  ex) 0.625

    @Column(name = "innings_pitched")
    var inningsPitched: BigDecimal? = null,               // NUMERIC(6,3)  ex) 65.333

    @Column(name = "hits")
    var hits: Int? = null,

    @Column(name = "home_runs")
    var homeRuns: Int? = null,

    @Column(name = "walks")
    var walks: Int? = null,

    @Column(name = "hit_by_pitch")
    var hitByPitch: Int? = null,

    @Column(name = "strike_outs")
    var strikeOuts: Int? = null,

    @Column(name = "runs")
    var runs: Int? = null,

    @Column(name = "earned_runs")
    var earnedRuns: Int? = null,

    @Column(name = "whip")
    var whip: BigDecimal? = null                          // NUMERIC(6,3)
)
