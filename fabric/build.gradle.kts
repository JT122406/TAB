plugins {
    id("dev.architectury.loom")
    id("architectury-plugin") version "3.4-SNAPSHOT"
}

architectury {
    platformSetupLoomIde()
    fabric()
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
    getByName("developmentFabric").extendsFrom(configurations["common"])
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
    maven("https://maven.parchmentmc.org")
}

@Suppress("UnstableApiUsage")
dependencies {
    minecraft("com.mojang:minecraft:1.21.5-rc2")
    mappings(loom.layered{
        officialMojangMappings()
        parchment("org.parchmentmc.data:parchment-1.21.4:2025.03.23@zip")
    })
    api(projects.shared)
    "common"(project(":modded", "namedElements")) { isTransitive = false }
    "shadowBundle"(project(":modded", "transformProductionFabric"))
    modImplementation("me.lucko:fabric-permissions-api:0.2-SNAPSHOT")
    modImplementation("eu.pb4:placeholder-api:2.5.0+1.21.2")
    modImplementation("net.fabricmc:fabric-loader:0.15.10")
    val version = "0.100.1+1.21"
    modImplementation(fabricApi.module("fabric-lifecycle-events-v1", version))
    modImplementation(fabricApi.module("fabric-networking-api-v1", version))
    modImplementation(fabricApi.module("fabric-entity-events-v1", version))
    modImplementation(fabricApi.module("fabric-command-api-v2", version))
}

tasks {
    compileJava {
        options.release.set(17)
    }
    validateAccessWidener {
        enabled = true
    }
    remapJar {
        injectAccessWidener.set(true)
    }
}
