plugins {
    id("dev.architectury.loom")
    id("architectury-plugin") version "3.4-SNAPSHOT"
}

architectury {
    common("fabric", "neoforge", "forge")
    platformSetupLoomIde()
}

loom.accessWidenerPath.set(file("src/main/resources/tab.accesswidener"))

repositories {
    // Gradle doesn't support combining settings and project repositories, so we have to re-declare all the settings repos we need
    maven("https://jitpack.io") // YamlAssist
    maven("https://repo.opencollab.dev/maven-snapshots/")
    maven("https://repo.viaversion.com/")
    maven("https://oss.sonatype.org/content/repositories/snapshots")
    maven("https://maven.nucleoid.xyz/")
    maven("https://maven.neoforged.net/releases")
    maven("https://maven.parchmentmc.org")
}

@Suppress("UnstableApiUsage")
dependencies {
    minecraft("com.mojang:minecraft:1.21.4")
    mappings(loom.layered{
        officialMojangMappings()
        parchment("org.parchmentmc.data:parchment-1.21.4:2025.03.23@zip")
    })
    compileOnly("org.jetbrains:annotations:26.0.2")
    api(projects.shared)
}

tasks {
    compileJava {
        options.release.set(21)
    }
}