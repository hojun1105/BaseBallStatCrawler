package com.demo.dto

import java.time.LocalDate

data class HitterStat(
    val playerId: Int,
    val date: LocalDate,
    val average: Double,
    val games: Int,
    val plateAppearances: Int,
    val atBat: Int,
    val runs: Int,
    val hits: Int,
    val doubleBase: Int,
    val tripleBase: Int,
    val homeRun: Int,
    val totalBase: Int,
    val runsBattedIn: Int,
    val sacrificeHit: Int,
    val sacrificeFly: Int,
    val basedOnBalls: Int,
    val intentionalBaseOnBalls: Int,
    val hitByPitch: Int,
    val strikeOut: Int,
    val groundedIntoDoublePlay: Int,
    val sluggingPercentage: Double,
    val onbasePercentage: Double,
    val onBasePlusSlugging: Double,
    val multiHit: Int,
    val runnersInScoringPosition: Double,
    val substituteHitterBattingAverage: Double
)