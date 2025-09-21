package com.demo.com.demo.dto

import java.time.LocalDate

data class PitcherStat(
    val playerId: Int,
    val date: LocalDate,
    val earnedRunAverage: Double,
    val games: Int,
    val wins: Int,
    val loses: Int,
    val save: Int,
    val hold: Int,
    val winningPercentage: Double,
    val inningsPitched: Double,
    val hits: Int,
    val homeRun: Int,
    val basedOnBalls: Int,
    val hitByPitch: Int,
    val strikeOut: Int,
    val runs: Int,
    val earnedRun: Int,
    val walksPlusHitsDividedByInningsPitched: Double,
    val completeGame: Int,
    val shutouts: Int,
    val qualityStart: Int,
    val blownSave: Int,
    val totalBattersFaced: Int,
    val numberOfPitches: Int,
    val average: Double,
    val doubleBase: Int,
    val tripleBase: Int,
    val sacrificeHit: Int,
    val sacrificeFly: Int,
    val intentionalBasedOnBalls: Int,
    val wildPitch: Int,
    val balk: Int
)