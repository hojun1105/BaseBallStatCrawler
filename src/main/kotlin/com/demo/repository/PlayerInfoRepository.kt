package com.demo.com.demo.repository


import com.demo.model.PlayerInfo
import com.demo.model.TeamInfo

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface PlayerInfoRepository: JpaRepository<PlayerInfo, Long> {

    fun findByNameAndTeam(name: String, team: TeamInfo): PlayerInfo?

}