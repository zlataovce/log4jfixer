package me.kcra.log4jfixer.utils

import java.io.File
import java.nio.file.*
import java.nio.file.attribute.BasicFileAttributes

class JarPatcher(private val patchable: File) {
    private val tempFolder: File = Files.createTempDirectory("jar_work").toFile()

    fun patch(classes: Map<String, File>): File {
        println("Extracting " + patchable.path + "...")
        unzip(patchable, tempFolder)
        Files.walkFileTree(tempFolder.toPath(), object : SimpleFileVisitor<Path>() {
            override fun visitFile(file: Path, attrs: BasicFileAttributes): FileVisitResult {
                val targetFile: File = file.toFile()
                when (targetFile.name) {
                    "JmsAppender\$1.class",
                    "JmsAppender\$Builder.class",
                    "JmsAppender.class",
                    "JndiManager\$1.class",
                    "JndiManager\$JndiManagerFactory.class",
                    "JndiManager.class",
                    "NetUtils.class" -> {
                        if (targetFile.path.contains("log4j")) {
                            Files.copy(classes[targetFile.name]!!.toPath(), file, StandardCopyOption.REPLACE_EXISTING)
                            println("Replaced file " + tempFolder.toPath().relativize(file).toString() + ".")
                        }
                    }
                    else -> {
                        if (targetFile.name.endsWith(".jar")) {
                            println("Patching nested JAR " + tempFolder.toPath().relativize(file).toString() + "...")
                            Files.move(JarPatcher(targetFile).patch(classes).toPath(), targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING)
                        }
                    }
                }
                return FileVisitResult.CONTINUE
            }
        })
        println("Compressing " + tempFolder.path + "...")
        return zip(tempFolder.toPath(), File.createTempFile("patched_jar", ".jar"))
    }
}