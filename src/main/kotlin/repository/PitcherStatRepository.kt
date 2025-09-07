package com.demo.repository

import com.demo.model.HitterStat
import com.demo.model.PitcherStat
import org.springframework.data.jpa.repository.JpaRepository

interface PitcherStatRepository: JpaRepository<PitcherStat, Long>