plugins {
    id("java")
}

group = "net.mangolise"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    mavenLocal()
    maven("https://maven.serble.net/snapshots/")
    maven("https://jitpack.io")
}

dependencies {
    implementation("net.mangolise:mango-game-sdk:latest")
    implementation("net.mangolise:mango-combat:latest")
    implementation("net.minestom:minestom-snapshots:d0754f2a15")
//    implementation("com.github.Minestom:VanillaReimplementation:9e3bd46bdd")
//    implementation("com.github.Minestom.VanillaReimplementation:core:9e3bd46bdd")
//    implementation("com.github.Minestom.VanillaReimplementation:datapack-loading:9e3bd46bdd")
//    implementation("com.github.Minestom.VanillaReimplementation:mojang-data:9e3bd46bdd")

    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}