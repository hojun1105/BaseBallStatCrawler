package com.demo.model

import jakarta.persistence.*
import java.math.BigDecimal
import java.time.LocalDate

@Entity
@Table(
    name = "pitcher_stat",
    uniqueConstraints = [UniqueConstraint(columnNames = ["player_id", "\"date\""])]
)
open class PitcherStat(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    open var id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
        name = "player_id",
        referencedColumnName = "id",
        nullable = false
    )
    open var player: PlayerInfo? = null,

    @Column(name = "\"date\"", nullable = false)  // 따옴표 컬럼
    open var date: LocalDate? = null,

    @Column(name = "rank")
    open var rank: Int? = null,

    @Column(name = "era")
    open var era: BigDecimal? = null,  // NUMERIC(5,2)

    @Column(name = "games")
    open var games: Int? = null,

    @Column(name = "wins")
    open var wins: Int? = null,

    @Column(name = "losses")
    open var losses: Int? = null,

    @Column(name = "saves")
    open var saves: Int? = null,

    @Column(name = "holds")
    open var holds: Int? = null,

    @Column(name = "winning_percentage")
    open var winningPercentage: BigDecimal? = null,  // NUMERIC(5,3)

    @Column(name = "innings_pitched")
    open var inningsPitched: BigDecimal? = null,     // NUMERIC(6,3)

    @Column(name = "hits")
    open var hits: Int? = null,

    @Column(name = "home_runs")
    open var homeRuns: Int? = null,

    @Column(name = "walks")
    open var walks: Int? = null,

    @Column(name = "hit_by_pitch")
    open var hitByPitch: Int? = null,

    @Column(name = "strike_outs")
    open var strikeOuts: Int? = null,

    @Column(name = "runs")
    open var runs: Int? = null,

    @Column(name = "earned_runs")
    open var earnedRuns: Int? = null,

    @Column(name = "whip")
    open var whip: BigDecimal? = null
)
