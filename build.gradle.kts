plugins {
    id("java")
    id("maven-publish")
    id("io.github.goooler.shadow") version("8.1.7")
}

var versionStr = System.getenv("GIT_COMMIT") ?: "dev"

group = "net.mangolise"
version = versionStr

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

publishing {
    repositories {
        maven {
            name = "serbleMaven"
            url = uri("https://maven.serble.net/snapshots/")
            credentials {
                username = System.getenv("SERBLE_REPO_USERNAME") ?: ""
                password = System.getenv("SERBLE_REPO_PASSWORD") ?: ""
            }
            authentication {
                create<BasicAuthentication>("basic")
            }
        }
    }

    publications {
        create<MavenPublication>("mavenGitCommit") {
            groupId = "net.copokbl"
            artifactId = "uhcstom"
            version = versionStr
            from(components["java"])
        }

        create<MavenPublication>("mavenLatest") {
            groupId = "net.mangolise"
            artifactId = "uhcstom"
            version = "latest"
            from(components["java"])
        }
    }
}

tasks.test {
    useJUnitPlatform()
}

java {
    withSourcesJar()
}

tasks.withType<Jar> {
    manifest {
        // Change this to your main class
        attributes["Main-Class"] = "net.copokbl.uhc.Test"
    }
}
