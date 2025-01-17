import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    id("java")
    id("com.gradleup.shadow") version "8.3.0"
    id("io.papermc.paperweight.userdev") version "1.7.2"
}

group = "com.tomushimano"
version = "${project.properties["version"]}-${commitHashShort()}"

repositories {
    mavenCentral()
}

dependencies {
    // Platform
    paperweight.paperDevBundle("1.21.1-R0.1-SNAPSHOT")

    // DI Framework
    implementation("com.google.dagger:dagger:2.52") {
        // javax.inject will be present at runtime, we don't need to include it in our jar
        exclude("javax.inject")
    }
    annotationProcessor("com.google.dagger:dagger-compiler:2.52")

    // Libs
    implementation(fileTree("./libs") {
        include("*.jar")
    })
    implementation("com.zaxxer:HikariCP:5.1.0") {
        exclude("org.slf4j")
    }
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
}

tasks.processResources {
    filesMatching("paper-plugin.yml") {
        expand("version" to (project.version))
    }
}

tasks.named<ShadowJar>("shadowJar") {
    minimize()
    val relocations = mapOf(
        "grapefruit.command" to "com.tomushimano.waypoint.reloc.command",
        "dagger" to "com.tomushimano.waypoint.reloc.dagger",
        "jakarta" to "com.tomushimano.waypoint.reloc.jakarta",
        "com.zaxxer.hikari" to "com.tomushimano.waypoint.reloc.hikari"
    )

    relocations.forEach {
        relocate(it.key, it.value)
    }

    archiveFileName.set("${project.name}-${project.version}.jar")
}

// Constants
object C {
    const val GIT_FOLDER = ".git"
    const val HASH_LENGTH = 7
    const val GIT_HEAD = "HEAD"
    const val REFS_PREFIX = "ref: "
}

fun commitHashShort(): String {
    val gitFolder = "$projectDir/${C.GIT_FOLDER}"
    val headContent = File("$gitFolder/${C.GIT_HEAD}").readText(Charsets.UTF_8)
    val isCommit = !headContent.startsWith(C.REFS_PREFIX)

    if (isCommit) return headContent.trim().substring(0, C.HASH_LENGTH)

    val refContent = File("$gitFolder/${headContent.split(":")[1].trim()}").readText(Charsets.UTF_8)
    return refContent.trim().substring(0, C.HASH_LENGTH)
}
