plugins {
    kotlin("jvm") version "2.1.10"
}

group = "com.github.zimablue.pufftower"
version = "1.0-SNAPSHOT"

repositories {
    mavenLocal()
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    compileOnly(fileTree("libs"))
    compileOnly("com.github.zimablue.devoutserver:DevoutServer:1.0-SNAPSHOT")
    compileOnly("com.github.zimablue.attrsystem:AttributeSystem-Minestom:1.0-SNAPSHOT")
    implementation("dev.hollowcube:polar:1.14.7")
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(22)
}