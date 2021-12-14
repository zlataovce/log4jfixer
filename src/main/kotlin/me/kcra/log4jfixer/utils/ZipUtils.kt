package me.kcra.log4jfixer.utils

import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.nio.file.*
import java.nio.file.attribute.BasicFileAttributes
import java.util.*
import java.util.zip.ZipEntry
import java.util.zip.ZipFile
import java.util.zip.ZipOutputStream

fun unzip(file: File, dest: File) {
    ZipFile(file).use { zipFile ->
        val iter: Enumeration<out ZipEntry> = zipFile.entries()
        while (iter.hasMoreElements()) {
            val entry: ZipEntry = iter.nextElement()
            if (entry.isDirectory) {
                continue
            }
            if (entry.name.contains("..")) {
                throw IOException("Entry is outside of target directory!")
            }
            val entryFile: File = Path.of(dest.absolutePath, entry.name).toFile()
            entryFile.mkdirs()
            Files.copy(zipFile.getInputStream(entry), entryFile.toPath(), StandardCopyOption.REPLACE_EXISTING)
        }
    }
}

fun zip(sourceDir: Path, zipFile: File): File {
    ZipOutputStream(FileOutputStream(zipFile)).use { outputStream ->
        Files.walkFileTree(sourceDir, object : SimpleFileVisitor<Path>() {
            override fun visitFile(file: Path, attrs: BasicFileAttributes): FileVisitResult {
                val targetFile: Path = sourceDir.relativize(file)
                outputStream.putNextEntry(ZipEntry(targetFile.toString()))
                val bytes: ByteArray = Files.readAllBytes(file)
                outputStream.write(bytes, 0, bytes.size)
                outputStream.closeEntry()
                return FileVisitResult.CONTINUE
            }
        })
    }
    return zipFile
}