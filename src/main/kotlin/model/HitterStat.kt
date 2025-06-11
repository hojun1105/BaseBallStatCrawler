package com.demo.model

import jakarta.persistence.*
import lombok.Getter
import lombok.NoArgsConstructor
import lombok.Setter
import java.time.LocalDate


@Entity
@Table(
    name = "hitter_stat",
    schema = "kbo",
    uniqueConstraints = [UniqueConstraint(columnNames = ["player_id", "date"])]
)
@Getter
@Setter
@NoArgsConstructor
class HitterStat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private val id: Long? = null

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "player_id", nullable = false)
    var player: PlayerInfo? = null

    @Column(name = "date")
    var date: LocalDate? = null

    @Column(name = "average")
    var average: Double? = null

    @Column(name = "games")
     var games: Int? = null

    @Column(name = "plate_appearances")
     var plateAppearances: Int? = null

    @Column(name = "at_bat")
     var atBat: Int? = null

    @Column(name = "runs")
     var runs: Int? = null

    @Column(name = "hits")
     var hits: Int? = null

    @Column(name = "double_base")
     var doubleBase: Int? = null

    @Column(name = "triple_base")
     var tripleBase: Int? = null

    @Column(name = "home_run")
     var homeRun: Int? = null

    @Column(name = "total_base")
     var totalBase: Int? = null

    @Column(name = "runs_batted_in")
     var runsBattedIn: Int? = null

    @Column(name = "stolen_base")
    var stolenBase: Int? = null

    @Column(name = "caught_stealing")
    var caughtStealing: Int? = null

    @Column(name = "sacrifice_hit")
     var sacrificeHit: Int? = null

    @Column(name = "sacrifice_fly")
     var sacrificeFly: Int? = null

    @Column(name = "based_on_balls")
     var basedOnBalls: Int? = null

    @Column(name = "intentional_based_on_balls")
     var intentionalBasedOnBalls: Int? = null

    @Column(name = "hit_by_pitch")
     var hitByPitch: Int? = null

    @Column(name = "strike_out")
     var strikeOut: Int? = null

    @Column(name = "grounded_into_double_play")
     var groundedIntoDoublePlay: Int? = null

    @Column(name = "slugging_percentage")
     var sluggingPercentage: Double? = null

    @Column(name = "on_base_percentage")
     var onBasePercentage: Double? = null

    @Column(name = "error")
    var error: Int? = null

    @Column(name="stolen_base_percentage")
    var stolenBasePercentage: Double? = null

    @Column(name = "multi_hit")
     var multiHit: Int? = null

    @Column(name = "on_base_plus_slugging")
    var onBasePlusSlugging: Double? = null

    @Column(name = "runners_in_scoring_position")
     var runnersInScoringPosition: Double? = null

    @Column(name = "substitute_hitter_batting_average")
     var substituteHitterBattingAverage: Double? = null
}