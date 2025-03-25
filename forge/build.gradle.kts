plugins {
    id("dev.architectury.loom")
    id("architectury-plugin") version "3.4-SNAPSHOT"
}

architectury {
    platformSetupLoomIde()
    forge()
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
    getByName("developmentForge").extendsFrom(configurations["common"])
    "shadowBundle" {
        isCanBeResolved = true
        isCanBeConsumed = false
    }
}

repositories {
    // Gradle doesn't support combining settings and project repositories, so we have to re-declare all the settings repos we need
    maven("https://jitpack.io") // YamlAssist
    maven("https://repo.opencollab.dev/maven-snapshots/")
    maven("https://repo.viaversion.com/")
    maven("https://oss.sonatype.org/content/repositories/snapshots")
    maven("https://maven.nucleoid.xyz/")
    maven("https://maven.neoforged.net/releases")
    maven("https://maven.minecraftforge.net/")
    maven("https://maven.parchmentmc.org")
}

@Suppress("UnstableApiUsage")
dependencies {
    minecraft("com.mojang:minecraft:1.21.4")
    mappings(loom.layered{
        officialMojangMappings()
        parchment("org.parchmentmc.data:parchment-1.21.4:2025.03.23@zip")
    })
    forge("net.minecraftforge:forge:1.21.4-54.1.3")
    api(projects.shared)
    "common"(project(":modded", "namedElements")) { isTransitive = false }
    "shadowBundle"(project(":modded", "transformProductionForge"))
}

loom.forge.accessTransformer(file("src/main/resources/META-INF/accesstransformer.cfg"))

tasks {
    compileJava {
        options.release.set(21)
    }
}
