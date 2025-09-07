package com.demo.util

import java.math.BigDecimal
import java.time.LocalDate
import java.time.format.DateTimeFormatter

object Parsers {
    fun parseKoreanDate(dateString: String?): LocalDate? =
        dateString?.takeIf { it.isNotBlank() }?.let {
            LocalDate.parse(it, DateTimeFormatter.ofPattern("yyyy년 MM월 dd일"))
        }

    fun parseSalary(salaryString: String?): Long? =
        salaryString?.replace("[^\\d]".toRegex(), "")?.takeIf { it.isNotBlank() }?.toLongOrNull()

    fun extractNumber(text: String?): Int? =
        text?.replace("[^\\d]".toRegex(), "")?.takeIf { it.isNotBlank() }?.toIntOrNull()

    fun parsePercentage(value: String): Double? =
        when {
            value.trim() == "-" -> null
            value.contains("%") -> value.replace("%", "").trim().toDoubleOrNull()?.div(100)
            else -> value.trim().toDoubleOrNull()
        }

    fun parseToBigDecimal(value: String): BigDecimal? =
        when{
            value.trim() == "-" -> null
            else -> value.trim().toBigDecimalOrNull()
        }
}