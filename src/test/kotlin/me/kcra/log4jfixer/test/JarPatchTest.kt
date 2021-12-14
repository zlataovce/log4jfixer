package me.kcra.log4jfixer.test

import me.kcra.log4jfixer.getFromURL
import me.kcra.log4jfixer.utils.JarPatcher
import me.kcra.log4jfixer.utils.unzip
import org.junit.jupiter.api.Test
import java.io.File
import java.nio.file.Files

internal class JarPatchTest {
    @Test
    fun patch() {
        val file: File = getFromURL("https://launcher.mojang.com/v1/objects/a16d67e5807f57fc4e550299cf20226194497dc2/server.jar")
        val log4jJarExtract: File = unzip(getFromURL("https://repo1.maven.org/maven2/org/apache/logging/log4j/log4j-core/2.15.0/log4j-core-2.15.0.jar"), Files.createTempDirectory("log4j_unzip").toFile())
        println("Pulling https://repo1.maven.org/maven2/org/apache/logging/log4j/log4j-core/2.15.0/log4j-core-2.15.0.jar...")
        val patchableClasses: Map<String, File> = Files.walk(log4jJarExtract.toPath())
            .map { path -> path.toFile() }
            .filter { f -> f.name.endsWith(".class") }
            .toList()
            .associateBy({ it.name }, { it })

        JarPatcher(file).patch(patchableClasses, "log4j")
    }
}