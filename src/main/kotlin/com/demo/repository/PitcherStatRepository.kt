package com.demo.com.demo.repository

import com.demo.model.PitcherStat
import org.springframework.data.jpa.repository.JpaRepository

interface PitcherStatRepository: JpaRepository<PitcherStat, Long>