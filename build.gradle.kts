plugins {
    kotlin("jvm") version "2.2.0"
    kotlin("plugin.serialization") version "2.1.10"
    id("application")
}

application {
    mainClass.set("bookmcp.McpServerKt")
}

group = "org.threedotz"
version = "1.0-SNAPSHOT"

val ktorVersion = "3.1.1"
repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    implementation("io.modelcontextprotocol:kotlin-sdk:0.4.0")
    implementation("io.ktor:ktor-client-core:$ktorVersion")
    implementation("io.ktor:ktor-client-cio:$ktorVersion")
    implementation("io.ktor:ktor-client-content-negotiation:$ktorVersion")
    implementation("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")
    implementation("org.slf4j:slf4j-nop:2.0.9")

    testImplementation(kotlin("test"))
    testImplementation("io.ktor:ktor-client-mock:$ktorVersion")
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(17)
}