plugins {
    kotlin("jvm") version "2.0.21"
}

group = "com.demo"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    implementation("org.jsoup:jsoup:1.17.2")
}

tasks.test {
    useJUnitPlatform()
}