package com.demo.repository

import com.demo.model.NaverStore
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface NaverStoreRepository : JpaRepository<NaverStore, Long> {
    fun findByNaverPlaceId(naverPlaceId: String): NaverStore?
    fun findByLocation(location: String): List<NaverStore>
}


