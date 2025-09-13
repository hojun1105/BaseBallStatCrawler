plugins {
    kotlin("jvm") version "1.9.24"                 // ★ 2.0.x → 1.9.24
    kotlin("plugin.spring") version "1.9.24"       // ★ 추가
    kotlin("plugin.jpa") version "1.9.24"          // ★ (엔티티 no-arg/open)
    id("org.springframework.boot") version "3.3.2" // ★ 3.2.0 → 3.3.x 권장
    id("io.spring.dependency-management") version "1.1.5"
}

group = "com.demo"
version = "1.0-SNAPSHOT"

repositories { mavenCentral() }

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-jdbc")

    // DB
    implementation("org.postgresql:postgresql")   // 부트 BOM이 버전 관리(42.7.x)

    // 크롤링
    implementation("org.jsoup:jsoup:1.17.2")
    implementation("org.seleniumhq.selenium:selenium-java:4.21.0")
    implementation("io.github.bonigarcia:webdrivermanager:5.9.2") // ★ 드라이버 자동 매칭

    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.5.0")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
}

kotlin { jvmToolchain(17) }   // JDK 17 유지