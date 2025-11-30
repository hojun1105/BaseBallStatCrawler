package com.demo.config

import org.springframework.boot.context.properties.ConfigurationProperties

/**
 * 네이버 지도 크롤링에 필요한 설정값을 정의하는 클래스
 */
@ConfigurationProperties(prefix = "naver.crawler")
class CrawlerConfig {
    var chromeDriverPath: String = "C:/chromedriver/chromedriver.exe"

    // Iframe ID
    val iframeSearch = "searchIframe" // 검색 결과 목록 iframe
    val iframeDetail = "entryIframe"  // 가게 상세 정보 iframe

    // 목록 페이지 선택자
    val scrollableElement = "div.Ryr1F" // 스크롤이 가능한 영역
    val storeListItem = "li.UEzoS"           // 가게 목록의 각 항목 (기본)
    val storeNameInList = "span.TYaxT"        // 가게 이름 (기본)
    val storeListItemFallback = "li.VLTHu"    // 가게 목록 항목 (대체)
    val storeNameInListFallback = "span.YwYLL" // 가게 이름 (대체)

    // 상세 페이지 선택자
    val detailViewContainer = "div.zD5Nm"      // 상세 정보 컨테이너
    val categoryInDetail = "span.lnJFt"         // 카테고리
    val addressInDetail = "span.LDgIH"          // 주소
    val phoneInDetail = "span.xlx7Q"            // 전화번호
    val reviewCountLink = "span.PXMot"          // 리뷰 개수
    val operatingInfoSection = "div.O8qbU"      // 영업시간 전체 섹션
    val operatingHoursMoreButton = "a.gKP9i"    // 영업시간 '펼쳐보기' 버튼
    val operatingHoursDayItem = "div.w9QyJ"     // 요일별 영업시간 한 줄
    val operatingHoursDayOfWeek = "span.i8cJw"  // 요일 텍스트 (예: '수')
    val operatingHoursTime = "div.H3ua4"        // 시간 텍스트 (예: '17:00 - 01:30')

    // 페이지 넘김
    val nextPageButton = "//a[contains(@class, 'mBN2s') and text()='%d']"
}

