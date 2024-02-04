pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.5.0"
}

rootProject.name = "kc-patchs"
include("gen-combinator")
include("kafka-event-listener")
include("dynamic-user-federation")

