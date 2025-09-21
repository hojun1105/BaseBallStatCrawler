package com.demo.model

import jakarta.persistence.*
import java.time.LocalDate

@Entity
@Table(
    name = "hitter_stat",
    uniqueConstraints = [UniqueConstraint(columnNames = ["player_id", "date"])]
)
open class HitterStat(

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

    @Column(name = "date")
    open var date: LocalDate? = null,

    @Column(name = "average")
    open var average: Double? = null,

    @Column(name = "games")
    open var games: Int? = null,

    @Column(name = "plate_appearances")
    open var plateAppearances: Int? = null,

    @Column(name = "at_bat")
    open var atBat: Int? = null,

    @Column(name = "runs")
    open var runs: Int? = null,

    @Column(name = "hits")
    open var hits: Int? = null,

    @Column(name = "double_base")
    open var doubleBase: Int? = null,

    @Column(name = "triple_base")
    open var tripleBase: Int? = null,

    @Column(name = "home_run")
    open var homeRun: Int? = null,

    @Column(name = "total_base")
    open var totalBase: Int? = null,

    @Column(name = "runs_batted_in")
    open var runsBattedIn: Int? = null,

    @Column(name = "stolen_base")
    open var stolenBase: Int? = null,

    @Column(name = "caught_stealing")
    open var caughtStealing: Int? = null,

    @Column(name = "sacrifice_hit")
    open var sacrificeHit: Int? = null,

    @Column(name = "sacrifice_fly")
    open var sacrificeFly: Int? = null,

    @Column(name = "based_on_balls")
    open var basedOnBalls: Int? = null,

    @Column(name = "intentional_based_on_balls")
    open var intentionalBasedOnBalls: Int? = null,

    @Column(name = "hit_by_pitch")
    open var hitByPitch: Int? = null,

    @Column(name = "strike_out")
    open var strikeOut: Int? = null,

    @Column(name = "grounded_into_double_play")
    open var groundedIntoDoublePlay: Int? = null,

    @Column(name = "slugging_percentage")
    open var sluggingPercentage: Double? = null,

    @Column(name = "on_base_percentage")
    open var onBasePercentage: Double? = null,

    @Column(name = "error")
    open var error: Int? = null,

    @Column(name = "stolen_base_percentage")
    open var stolenBasePercentage: Double? = null,

    @Column(name = "multi_hit")
    open var multiHit: Int? = null,

    @Column(name = "on_base_plus_slugging")
    open var onBasePlusSlugging: Double? = null,

    @Column(name = "runners_in_scoring_position")
    open var runnersInScoringPosition: Double? = null,

    @Column(name = "substitute_hitter_batting_average")
    open var substituteHitterBattingAverage: Double? = null
)
