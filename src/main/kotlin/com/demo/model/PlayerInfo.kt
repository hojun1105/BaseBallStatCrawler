package com.demo.model

import jakarta.persistence.*
import java.time.LocalDate

@Entity
@Table(
    name = "player_info",
    uniqueConstraints = [UniqueConstraint(columnNames = ["name", "team_id"])]
)
open class PlayerInfo(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    open var id: Long? = null,

    @Column(name = "name", nullable = false, length = 50)
    open var name: String? = null,

    @Column(name = "position", length = 10)
    open var position: String? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id", nullable = false)
    open var team: TeamInfo? = null,

    @Column(name = "back_number")
    open var backNumber: Int? = null,

    @Column(name = "birth_date")
    open var birthDate: LocalDate? = null,

    @Column(name = "height_weight")
    open var heightWeight: String? = null,

    @Column(name = "salary")
    open var salary: Long? = null,

    @Column(name = "debut_year")
    open var debutYear: Int? = null,

    @OneToMany(mappedBy = "player", cascade = [CascadeType.ALL], orphanRemoval = true)
    open var hitterStats: MutableList<HitterStat> = mutableListOf(),

    @OneToMany(mappedBy = "player", cascade = [CascadeType.ALL], orphanRemoval = true)
    open var pitcherStats: MutableList<PitcherStat> = mutableListOf()
)
