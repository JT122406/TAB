dependencies {
    implementation(projects.bukkit)
    compileOnly("org.spigotmc:spigot:1.21.5-R0.1-SNAPSHOT")
}

tasks.compileJava {
    options.release.set(21) // Included library "jtracy" requires Java 21
}