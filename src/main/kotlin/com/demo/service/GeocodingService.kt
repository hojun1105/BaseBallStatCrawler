package com.demo.service

import com.demo.config.ApiConfig
import com.demo.dto.Address
import com.demo.dto.Coordinates
import com.demo.dto.GeocodeResponse
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.serialization.json.Json
import org.springframework.stereotype.Service

/**
 * 네이버 Geocoding API를 호출하여 주소를 좌표로 변환하는 서비스 클래스
 */
@Service
class GeocodingService(
    private val httpClient: HttpClient,
    private val apiConfig: ApiConfig
) {
    private val geocodeUrl = "https://maps.apigw.ntruss.com/map-geocode/v2/geocode"

    /**
     * 주소 문자열을 받아 좌표를 반환합니다.
     * 원본 주소로 먼저 시도하고, 실패 시 정제된 주소로 재시도합니다.
     */
    suspend fun getCoordinates(address: String): Result<Coordinates> {
        // 1. 원본 주소로 먼저 시도
        val resultFromOriginal = callGeocodingApi(address)
        if (resultFromOriginal.isSuccess) {
            return resultFromOriginal
        }

        // 2. 원본 주소 실패 시, 정제된 주소로 재시도
        val cleanedAddress = normalizeAddress(address)

        // 원본과 정제된 주소가 동일하면 재시도할 필요 없음
        if (cleanedAddress == address) {
            return resultFromOriginal // 원래의 실패 결과를 반환
        }

        println("\n   -> 원본 주소 실패, 정제된 주소로 재시도: '$cleanedAddress'")
        return callGeocodingApi(cleanedAddress)
    }

    /**
     * 실제 Geocoding API를 호출하는 비공개(private) 헬퍼 함수
     */
    private suspend fun callGeocodingApi(addressQuery: String): Result<Coordinates> {
        return try {
            val httpResponse = httpClient.get(geocodeUrl) {
                header("X-NCP-APIGW-API-KEY-ID", apiConfig.clientId)
                header("X-NCP-APIGW-API-KEY", apiConfig.clientSecret)
                parameter("query", addressQuery)
            }

            if (httpResponse.status.value != 200) {
                val errorBody = httpResponse.bodyAsText()
                Result.failure(Exception("API 호출 실패 (HTTP ${httpResponse.status}): $errorBody"))
            } else {
                val responseText = httpResponse.bodyAsText()
                val geocodeResponse: GeocodeResponse = Json { ignoreUnknownKeys = true }.decodeFromString(responseText)
                
                if (geocodeResponse.status == "OK" && geocodeResponse.addresses.isNotEmpty()) {
                    val addr = geocodeResponse.addresses.first()
                    Result.success(Coordinates(latitude = addr.y.toDouble(), longitude = addr.x.toDouble()))
                } else {
                    Result.failure(Exception("주소를 찾을 수 없음 (API 응답: ${geocodeResponse.status})"))
                }
            }
        } catch (e: Exception) {
            Result.failure(Exception("좌표 변환 중 예외 발생: ${e.message}"))
        }
    }

    /**
     * API가 더 잘 인식하도록 주소 문자열에서 불필요한 부분을 제거합니다.
     */
    private fun normalizeAddress(address: String): String {
        return address
            .split(",")[0]
            .replace(Regex("\\(.*\\)"), "")
            .replace(Regex("[가-힣]+(점|본점)$"), "")
            .replace(Regex("\\s*\\d+층"), "")
            .replace(Regex("\\s*지하\\d*층?"), "")
            .replace(Regex("\\s*\\d+호"), "")
            .trim()
    }
}


