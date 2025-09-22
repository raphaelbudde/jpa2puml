package de.raphaelbudde.jpa2puml.classes

import org.apache.bcel.classfile.ClassParser
import org.apache.bcel.classfile.JavaClass
import org.slf4j.LoggerFactory
import java.io.File
import java.io.IOException
import java.util.jar.JarFile

class JavaClassFinder(
    private val excludedDirectoryPatterns: List<Regex> = listOf(),
    private val excludedClassNamePatterns: List<Regex> = listOf(),
) {
    companion object {
        val logger = LoggerFactory.getLogger(JavaClassFinder::class.java)!!

        const val CLASS_FILE_EXTENSION: String = ".class"
        const val JAR_FILE_EXTENSION: String = ".jar"
    }

    fun findClassFiles(classOrJarOrDirectory: File): Set<JavaClass> = findClassParserDeep(classOrJarOrDirectory)
        .mapNotNull { parser ->
            try {
                parser.parse()
            } catch (e: IOException) {
                logger.warn("parse failed", e)
                null
            }
        }.toSet()

    private fun findClassParserDeep(path: File): Set<ClassParser> = if (path.excluded()) {
        setOf()
    } else if (path.isDirectory) {
        (path.listFiles() ?: emptyArray())
            .flatMap { findClassParserDeep(it) }
            .toSet()
    } else if (path.name.endsWith(CLASS_FILE_EXTENSION)) {
        setOf(ClassParser(path.absolutePath))
    } else if (path.name.endsWith(JAR_FILE_EXTENSION)) {
        JarFile(path).use { jar ->
            jar
                .entries()
                .toList()
                .filter { it.name.endsWith(CLASS_FILE_EXTENSION) }
                .map { ClassParser(path.absolutePath, it.name) }
                .toSet()
        }
    } else {
        setOf()
    }

    private fun File.excluded(): Boolean = if (this.isDirectory) {
        excludedDirectoryPatterns.any {
            it.matches(this.path)
        }
    } else {
        excludedClassNamePatterns.any {
            it.matches(this.name)
        }
    }
}
