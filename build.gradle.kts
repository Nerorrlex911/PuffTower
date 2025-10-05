plugins {
    kotlin("jvm") version "2.1.10"
}

group = "com.github.zimablue.pufftower"
version = "1.0-SNAPSHOT"

repositories {
    mavenLocal()
    mavenCentral()
    maven { url = uri("https://reposilite.atlasengine.ca/public")}
}

dependencies {
    testImplementation(kotlin("test"))
    compileOnly(fileTree("libs"))
    compileOnly("com.github.zimablue.devoutserver:DevoutServer:1.0-SNAPSHOT")
    compileOnly("com.github.zimablue.attrsystem:AttributeSystem-Minestom:1.0-SNAPSHOT")
    implementation("dev.hollowcube:polar:1.14.7")
    implementation("ca.atlasengine:atlas-projectiles:2.1.5")
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(22)
}
tasks.jar {
    doLast {
        val destinationDir = file("F:\\Code\\MyCode\\MineStom\\Devout\\DevoutServerTest\\plugins") // 替换为目标路径
        copy {
            from(archiveFile)
            into(destinationDir)
        }
        println("Jar file copied to $destinationDir")
    }
}