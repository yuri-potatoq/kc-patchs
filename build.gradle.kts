plugins {
    kotlin("jvm") version "1.9.0"
    application
}

repositories {
    mavenCentral()
    maven(url = "https://packages.confluent.io/maven/")
}

val kcVersion by extra { "23.0.3" }
val gsonVersion by extra { "2.2.4" }
val kafkaVersion by extra { "2.1.0" }

group = "com.github.yuri-potatoq"
version = "1.0-SNAPSHOT"


dependencies {
    testImplementation(kotlin("test"))

    listOf(
        "server-spi",
        "server-spi-private",
        "services"
    ).map { compileOnly("org.keycloak:keycloak-$it:$kcVersion") }

    implementation("com.google.code.gson:gson:$gsonVersion")
    implementation("org.apache.kafka:kafka-clients:$kafkaVersion")
        // setup to avoid conflict with keycloak production "slf4j" dependency
        { exclude(group = "org.slf4j", module = "slf4j-api") }
}

tasks.withType<Jar> {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE

    // runtime dependency only will be loaded, to avoid bring keycloak dependencies
    from(configurations.runtimeClasspath.map { config ->
        config.map { if (it.isDirectory) it else zipTree(it) }
    })
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