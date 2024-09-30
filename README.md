# UHC (Minestom UHC gamemode)
This is a full UHC implementation using zero mojang code.
It uses [Mango Combat](https://github.com/Mangolise/mango-combat), 
meaning that this does not have Vanilla combat.

## Project State
This project is still in development and may not be 100% stable.
Because this is a Minestom project it requires implementing every
Minecraft mechanic from scratch, so if the feature isn't listed then
it probably won't work. This includes obvious things you take for granted
like having the void deal damage.

### Features
- Crafting
- Block Drops
- Combat ([with mango-combat](https://github.com/Mangolise/mango-combat))
- Lootable chests
- Using buckets and flint and steel
- Doors and trapdoors functioning
- Dropping items
- Fall damage
- Falling blocks
- Jukeboxes
- Liquid mechanics
- Scoreboard showing game status

## Building
Just execute the `shadowJar` gradle task. `./gradlew shadowJar`.

## Running Example
Build a copy of the project, and put an Anvil world (Nochian) named `uhc` next to it.
Run the jar and it should be joinable. An example world has been placed in the root
of this project, see `./uhc`.

## Using As A Library

Add the dependency:
<details>
<summary>
build.gradle.kts
</summary>

```kotlin
repositories {
    mavenCentral()
    maven("https://maven.serble.net/snapshots/")
}

dependencies {
implementation("net.copokbl:uhcstom:latest")
}
```
</details>

<details>
<summary>
build.gradle
</summary>

```groovy
repositories {
    maven { url 'https://maven.serble.net/snapshots/' }
}

dependencies {
    implementation 'net.copokbl:uhcstom:latest'
}
```
</details>

<details>
<summary>
pom.xml
</summary>

pom.xml
```xml
<repositories>
    <repository>
        <id>Serble</id>
        <url>https://maven.serble.net/snapshots/</url>
    </repository>
</repositories>

<dependencies>
    <dependency>
        <groupId>net.copokbl</groupId>
        <artifactId>uhcstom</artifactId>
        <version>latest</version>
    </dependency>
</dependencies>
```
</details>

Then you can use it like any other [Mango GameSDK](https://github.com/Mangolise/mango-game-sdk) game.
Create the `MinecraftServer`, create an instance of `Uhc` and pass in a config, run `.setup()` then
start the `MinecraftServer` and you're good to go! See the `Test` class for an example.