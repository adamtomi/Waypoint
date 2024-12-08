import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import java.nio.file.Files
import java.nio.file.Paths

plugins {
    id("java")
    id("com.gradleup.shadow") version "8.3.0"
    id("io.papermc.paperweight.userdev") version "1.7.2"
}

group = "com.tomushimano"
version = "1.0.0-ALPHA"

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
    implementation(files("libs/grapefruit-3.0.0-ALPHA.jar"))
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

    val commitHash = commitHashShort()
    archiveFileName.set("${project.name}-${project.version}-$commitHash.jar")
}

// Constants
object C {
    const val GIT_FOLDER = ".git"
    const val HASH_LENGTH = 7
    const val GIT_HEAD = "HEAD"
    const val REFS_PREFIX = "ref: "
}

fun commitHashShort(): String {
    val gitFolder = Paths.get("$projectDir/${C.GIT_FOLDER}")
    val head = gitFolder.resolve(C.GIT_HEAD)
    val content = Files.newBufferedReader(head).readText()
    val isCommit = !content.startsWith(C.REFS_PREFIX)

    if (isCommit) return content.trim().substring(0, C.HASH_LENGTH)

    val refHead = gitFolder.resolve(content.split(":")[1].trim())
    return Files.newBufferedReader(refHead).readText().trim().substring(0, C.HASH_LENGTH)
}
