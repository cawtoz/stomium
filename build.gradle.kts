plugins {
    kotlin("jvm") version "2.1.10"
}

group = "com.github.cawtoz"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("net.minestom:minestom-snapshots:39d445482f")
    testImplementation(kotlin("test"))
    implementation("org.slf4j:slf4j-simple:2.0.17")
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(23)
}