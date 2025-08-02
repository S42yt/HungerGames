import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val javaVersion = "21"
val mcVersion = "1.21"

group = "de.hglabor"
version = "${mcVersion}_v1"

description = "Minecraft Hunger Games in $mcVersion"

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.shadow)

    `java-library`
}

repositories {
    mavenLocal()
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://oss.sonatype.org/content/repositories/central")
    maven("https://oss.sonatype.org/content/groups/public/")
}

dependencies {
    implementation(libs.kotlin.stdlib)
    
    // Paper API
    compileOnly(libs.paper.api)

    // KSpigot (if still needed, otherwise remove)
    implementation(files("libs/kspigot-1.21.0.jar"))

    // KMONGO
    implementation(libs.kmongo)
    implementation(libs.kmongo.serialization)

    // Kyori Adventure API
    implementation(libs.adventure.api)

    // Kotlin Coroutines
    implementation(libs.kotlinx.coroutines.core)
    
    // Kotlin Serialization
    implementation(libs.kotlinx.serialization.json)
}

tasks {
    shadowJar {
        fun reloc(pkg: String) = relocate(pkg, "de.hglabor.dependency.$pkg")
        reloc("net.axay")
        //reloc("de.hglabor")
        mergeServiceFiles()
    }
    
    withType<KotlinCompile> {
        kotlinOptions {
            jvmTarget = javaVersion
            freeCompilerArgs += "-opt-in=kotlin.RequiresOptIn"
        }
    }
}
