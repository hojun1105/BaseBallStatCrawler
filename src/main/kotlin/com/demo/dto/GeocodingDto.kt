package com.demo.dto

import kotlinx.serialization.Serializable

/**
 * 네이버 Geocoding API의 JSON 응답 구조에 매핑되는 데이터 클래스
 */
@Serializable
data class GeocodeResponse(
    val status: String,
    val addresses: List<Address> = emptyList(),
    val errorMessage: String? = null
)

/**
 * Geocoding API 응답 내의 주소 정보를 담는 데이터 클래스
 */
@Serializable
data class Address(
    val x: String, // 경도 (longitude)
    val y: String  // 위도 (latitude)
)

/**
 * 최종 변환된 좌표(위도, 경도)를 담는 데이터 클래스
 */
data class Coordinates(
    val latitude: Double,
    val longitude: Double
)


