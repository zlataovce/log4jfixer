package me.kcra.log4jfixer

import me.kcra.log4jfixer.utils.JarPatcher
import me.kcra.log4jfixer.utils.unzip
import org.apache.commons.cli.CommandLine
import org.apache.commons.cli.DefaultParser
import org.apache.commons.cli.HelpFormatter
import org.apache.commons.cli.Options
import java.io.File
import java.net.URL
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption

fun main(args: Array<String>) {
    val patchableFiles: List<String> = listOf(
        "JmsAppender\$1.class", "JmsAppender\$Builder.class",
        "JmsAppender.class", "JndiManager\$1.class",
        "JndiManager\$JndiManagerFactory.class",
        "JndiManager.class", "NetUtils.class"
    )
    val options: Options = Options()
        .addRequiredOption("f", "file", true, "Selects the file to be patched")
    val helpFormatter = HelpFormatter()
    val cmd: CommandLine
    try {
        cmd = DefaultParser().parse(options, args)
    } catch (e: Exception) {
        helpFormatter.printHelp("Log4JFixer", options)
        return
    }

    val file: File = Path.of(cmd.getOptionValue('f')).toFile()
    if (!file.isFile) {
        println("Could not find patchable file!")
        return
    }
    val log4jJarExtract: File = Files.createTempDirectory("log4j_unzip").toFile()
    println("Pulling https://repo1.maven.org/maven2/org/apache/logging/log4j/log4j-core/2.16.0/log4j-core-2.16.0.jar...")
    unzip(getFromURL("https://repo1.maven.org/maven2/org/apache/logging/log4j/log4j-core/2.16.0/log4j-core-2.16.0.jar"), log4jJarExtract)
    val patchableClasses: Map<String, File> = Files.walk(log4jJarExtract.toPath())
        .map { path -> path.toFile() }
        .filter { f -> patchableFiles.contains(f.name) }
        .toList()
        .associateBy({ it.name }, { it })

    Files.move(JarPatcher(file).patch(patchableClasses).toPath(), Path.of(System.getProperty("user.dir"), "patched.jar"), StandardCopyOption.REPLACE_EXISTING)
}

fun getFromURL(url: String): File {
    val file: Path = Files.createTempFile("downloaded_file", null)
    URL(url).openStream().use { inputStream ->
        Files.copy(inputStream, file, StandardCopyOption.REPLACE_EXISTING)
    }
    return file.toFile()
}