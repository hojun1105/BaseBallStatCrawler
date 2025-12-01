package com.demo.service

import com.demo.config.ApiConfig
import com.demo.dto.Address
import com.demo.dto.Coordinates
import com.demo.dto.GeocodeResponse
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
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

    init {
        // API 키 로드 확인
        println("=== GeocodingService 초기화 ===")
        println("Client ID 길이: ${apiConfig.clientId.length}")
        println("Client ID (처음 20자): ${apiConfig.clientId}")
        println("Client Secret 길이: ${apiConfig.clientSecret.length}")
        println("Client Secret (처음 20자): ${apiConfig.clientSecret}")

        if (apiConfig.clientId.isEmpty() || apiConfig.clientSecret.isEmpty()) {
            println("⚠️ 경고: API 키가 비어있습니다!")
        }
    }

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
            // API 키 확인 및 trim
            val clientId = apiConfig.clientId.trim()
            val clientSecret = apiConfig.clientSecret.trim()
            
            println("🔍 API 호출 정보:")
            println("   URL: $geocodeUrl")
            println("   Query: $addressQuery")
            println("   Client ID: ${clientId.take(20)}...")
            println("   Client Secret: ${clientSecret.take(20)}...")
            
            if (clientId.isEmpty() || clientSecret.isEmpty()) {
                println("❌ API 키가 비어있습니다!")
                return Result.failure(Exception("네이버 API 키가 설정되지 않았습니다."))
            }
            
            // API 키 형식 확인
            if (clientId.startsWith("ncp_iam_") || clientSecret.startsWith("ncp_iam_")) {
                println("⚠️ 경고: IAM 키를 사용하고 있습니다!")
                println("   네이버 Geocoding API는 서비스 키(Client ID/Secret)를 사용해야 합니다.")
                println("   네이버 클라우드 플랫폼 콘솔에서 Geocoding API 서비스 키를 확인하세요.")
            }
            
            // Ktor에서 헤더를 설정하는 방법
            val httpResponse = httpClient.get(geocodeUrl) {
                url {
                    parameters.append("query", addressQuery)
                }
                headers {
                    append("X-NCP-APIGW-API-KEY-ID", clientId)
                    append("X-NCP-APIGW-API-KEY", clientSecret)
                }
            }
            
            // 헤더가 제대로 설정되었는지 확인
            println("📤 전송된 헤더 확인:")
            println("   X-NCP-APIGW-API-KEY-ID: ${clientId.take(30)}...")
            println("   X-NCP-APIGW-API-KEY: ${clientSecret.take(30)}...")

            println("📡 API 응답 상태: ${httpResponse.status.value}")

            if (httpResponse.status.value != 200) {
                val errorBody = httpResponse.bodyAsText()
                println("❌ API 호출 실패:")
                println("   Status: ${httpResponse.status.value}")
                println("   Response Body: $errorBody")
                
                // 401 오류의 경우 상세 정보 출력
                if (httpResponse.status.value == 401) {
                    println("⚠️ 401 Unauthorized - 인증 실패")
                    println("   가능한 원인:")
                    println("   1. API 키가 잘못되었습니다")
                    println("   2. IAM 키를 사용하고 있습니다 (서비스 키가 필요할 수 있음)")
                    println("   3. Geocoding API 서비스가 활성화되지 않았습니다")
                }
                
                Result.failure(Exception("API 호출 실패 (HTTP ${httpResponse.status.value}): $errorBody"))
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
            println("❌ 예외 발생: ${e.message}")
            e.printStackTrace()
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


