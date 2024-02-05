plugins {
    application
    kotlin("jvm") version "1.9.0"
}

group = "com.github.yuri-potatoq"
version = "1.0-SNAPSHOT"


repositories {
    mavenCentral()
}

dependencies {
    testImplementation("junit:junit:4.13")

    implementation("io.arrow-kt:arrow-core:1.2.0")

    val kotlinxHtmlVersion = "0.11.0"

    // include for JVM target
    implementation("org.jetbrains.kotlinx:kotlinx-html-jvm:$kotlinxHtmlVersion")
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(17)
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

