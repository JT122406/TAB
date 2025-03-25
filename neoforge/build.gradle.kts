plugins {
    id("dev.architectury.loom")
    id("architectury-plugin") version "3.4-SNAPSHOT"
}

architectury {
    platformSetupLoomIde()
    neoForge()
}

configurations {
    create("common")
    "common" {
        isCanBeResolved = true
        isCanBeConsumed = false
    }
    create("shadowBundle")
    compileClasspath.get().extendsFrom(configurations["common"])
    runtimeClasspath.get().extendsFrom(configurations["common"])
    getByName("developmentNeoForge").extendsFrom(configurations["common"])
    "shadowBundle" {
        isCanBeResolved = true
        isCanBeConsumed = false
    }
}

loom.accessWidenerPath.set(project(":modded").file("src/main/resources/tab.accesswidener"))

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
    neoForge("net.neoforged:neoforge:21.4.123")
    api(projects.shared)
    "common"(project(":modded", "namedElements")) { isTransitive = false }
    "shadowBundle"(project(":modded", "transformProductionNeoForge"))
}

tasks {
    compileJava {
        options.release.set(21)
    }
    shadowJar {
        configurations = listOf(project.configurations.getByName("shadowBundle"))
        archiveClassifier.set("dev-shadow")
    }

    remapJar {
        inputFile.set(shadowJar.get().archiveFile)
        dependsOn(shadowJar)
        atAccessWideners.add("tab.accesswidener")
    }
}
