package com.demo.com.demo.service

import com.demo.model.PitcherStat
import org.springframework.stereotype.Service
import javax.sql.DataSource

@Service
class SaveService(
    private val dataSource: DataSource // 또는 JdbcTemplate
) {
    fun saveHitterStats(stats: List<List<String>>) {
        val conn = dataSource.connection
        // Insert or Update SQL 실행
        conn.close()
    }

    fun savePitcherStats(stats: List<PitcherStat>) { /* ... */ }
}