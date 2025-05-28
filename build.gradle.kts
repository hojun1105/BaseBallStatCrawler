plugins {
    kotlin("jvm") version "2.0.21"
    application
}

group = "com.demo"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    implementation("org.jsoup:jsoup:1.17.2")
    implementation("org.seleniumhq.selenium:selenium-java:4.21.0")
    implementation("org.seleniumhq.selenium:selenium-chrome-driver:4.21.0")
    implementation("org.postgresql:postgresql:42.6.0")
}

application {
    mainClass.set("com.demo.MainKt")
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(17)
}
