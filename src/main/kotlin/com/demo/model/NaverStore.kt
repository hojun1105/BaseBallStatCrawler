package com.demo.model

import jakarta.persistence.*

/**
 * 네이버 지도에서 크롤링한 가게 정보를 담는 JPA 엔티티
 */
@Entity
@Table(name = "naver_stores", uniqueConstraints = [UniqueConstraint(columnNames = ["naver_place_id"])])
open class NaverStore(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    open var id: Long = 0,

    @Column(name = "naver_place_id", nullable = false, unique = true)
    open var naverPlaceId: String = "",

    @Column(name = "name", nullable = false, length = 255)
    open var name: String = "",

    @Column(name = "category", length = 100)
    open var category: String = "",

    @Column(name = "visitor_reviews", length = 50)
    open var visitorReviews: String = "",

    @Column(name = "blog_reviews", length = 50)
    open var blogReviews: String = "",

    @Column(name = "operating_hours", columnDefinition = "TEXT")
    open var operatingHours: String = "",

    @Column(name = "address", length = 255)
    open var address: String = "",

    @Column(name = "phone_num", length = 50)
    open var phoneNum: String = "",

    @Column(name = "search_keyword", length = 255)
    open var searchKeyword: String = "",

    @Column(name = "location", length = 255)
    open var location: String = "",

    @Column(name = "latitude")
    open var latitude: Double? = null,

    @Column(name = "longitude")
    open var longitude: Double? = null
) {
    constructor() : this(
        id = 0,
        naverPlaceId = "",
        name = "",
        category = "",
        visitorReviews = "",
        blogReviews = "",
        operatingHours = "",
        address = "",
        phoneNum = "",
        searchKeyword = "",
        location = "",
        latitude = null,
        longitude = null
    )
}

