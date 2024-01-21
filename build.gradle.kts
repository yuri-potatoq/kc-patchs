import com.google.protobuf.gradle.*

plugins {
    kotlin("jvm") version "1.9.0"
    id("com.google.protobuf") version "0.8.19"
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

    // protobuf stuff
    implementation("com.google.protobuf:protobuf-java:3.16.3")
    implementation("io.confluent:kafka-protobuf-serializer:5.5.1")
        { exclude(group = "org.slf4j", module = "slf4j-api") }

    protobuf(files("protobuf/"))
}

protobuf {
    protoc {
        // The artifact spec for the Protobuf Compiler
        artifact = "com.google.protobuf:protoc:3.16.3"
    }

    generateProtoTasks {
        all().forEach { task ->
            task.builtins {
                kotlin {}
            }
        }
    }
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