package com.demo.model

import jakarta.persistence.*

@Entity
@Table(name = "team_info", schema = "kbo")
open class TeamInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    open var id: Int? = null

    @Column(name = "name", nullable = false, length = 50)
    open var name: String? = null

    @Column(name = "logo")
    open var logo: String? = null

    @OneToMany(mappedBy = "team", cascade = [CascadeType.ALL], orphanRemoval = true)
    open var players: MutableList<PlayerInfo> = mutableListOf()
}
