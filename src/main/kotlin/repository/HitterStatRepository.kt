package com.demo.repository

import com.demo.model.HitterStat
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface HitterStatRepository : JpaRepository<HitterStat, Long>
