package com.demo.repository

import com.demo.model.TeamInfo
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface TeamInfoRepository: JpaRepository<TeamInfo, Long> {

    fun findByNameContaining(keyword: String): TeamInfo

}