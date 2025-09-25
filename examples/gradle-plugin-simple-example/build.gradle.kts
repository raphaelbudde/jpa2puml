plugins {
    kotlin("jvm") version "2.2.20"
    id("de.raphaelbudde.jpa2puml.jpa2puml-gradle-plugin") version "1.2.2-SNAPSHOT"
}

group = "de.raphaelbudde.jpa2puml"
version = "1.2.2-SNAPSHOT"

repositories {
    mavenLocal()
    mavenCentral()
}

dependencies {
    implementation("jakarta.persistence:jakarta.persistence-api:3.2.0")
}

