/*
 * Copyright 2010-2024 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.konan.test.blackbox.support.compilation

import org.jetbrains.kotlin.klib.KlibCompatibilityTestUtils
import org.jetbrains.kotlin.konan.target.TargetSupportException
import org.jetbrains.kotlin.konan.util.ArchiveType
import org.jetbrains.kotlin.konan.util.DependencyDownloader
import org.jetbrains.kotlin.konan.util.DependencyExtractor
import java.io.File
import java.io.InputStream
import java.io.InputStreamReader
import java.net.URL

private val konanDirectory = System.getProperty("user.home") + File.separator + ".konan"

private val compilerExecutable = File.separator + "bin" + File.separator + "kotlinc-native"

private val hostSpecificExtension: String
    get() {
        val javaOsName = System.getProperty("os.name")
        return when {
            javaOsName.startsWith("Windows") -> ".zip"
            else -> ".tar.gz"
        }
    }

internal fun getReleasedCompiler(version: String): ReleasedCompiler {
    val distributivePath = downloadIfRequired(version)
    val compilerPath = distributivePath.absolutePath + compilerExecutable
    return ReleasedCompiler(compilerPath)
}

class ReleasedCompiler(
    private val binaryPath: String,
) {
    private fun execute(args: List<String>) {
        val command = listOf(binaryPath) + args
        println("Execute: ${command.joinToString(" ")}")
        val builder = ProcessBuilder()
        builder.command(command)
        val process = builder.start()

        printOutput(process.inputStream)
        printOutput(process.errorStream)
        val exitCode = process.waitFor()
        println("Exited with code $exitCode")
    }

    fun buildKlib(sourceFiles: List<File>, dependencies: KlibCompatibilityTestUtils.Dependencies, outputFile: File) {
        execute(sourceFiles.map { it.absolutePath } + dependencies.toCompilerArgs() +
                        listOf(
                            "-produce", "library",
                            "-o", outputFile.absolutePath
                        )
        )
    }

    private fun KlibCompatibilityTestUtils.Dependencies.toCompilerArgs(): List<String> {
        return regularDependencies.flatMap { listOf("-library", it.libraryFile.absolutePath) } +
                friendDependencies.flatMap { listOf("-friend-modules", it.libraryFile.absolutePath) }
    }

    private fun printOutput(processOutput: InputStream) {
        InputStreamReader(processOutput).useLines { lines ->
            lines.forEach { println(it) }
        }
    }
}


private fun downloadIfRequired(version: String): File {
    val distributiveDirectoryName = "kotlin-native-prebuilt-${hostOs()}-${hostArch()}-$version"
    val targetDirectory = File(konanDirectory + File.separator + distributiveDirectoryName)
    if (targetDirectory.exists()) {
        println("Compiler directory already exists: ${targetDirectory.absolutePath}")
        return targetDirectory
    }

    val artifactFileName = "kotlin-native-${hostOs()}-${hostArch()}-$version"
    val downloadedFile = downloadCompiler(artifactFileName, version)

    DependencyExtractor(ArchiveType.systemDefault).extract(downloadedFile, File(konanDirectory))
    val unpackedDir = File(konanDirectory + File.separator + artifactFileName)
    unpackedDir.renameTo(targetDirectory)
    return targetDirectory
}

private fun downloadCompiler(artifactFileName: String, version: String): File {
    val releasedArtifactFileNameWithExtension = artifactFileName + hostSpecificExtension

    val tempLocation = File(System.getProperty("java.io.tmpdir") + File.separator + releasedArtifactFileNameWithExtension)
    val url = URL("https://github.com/JetBrains/kotlin/releases/download/v$version/$releasedArtifactFileNameWithExtension")

    return DependencyDownloader(customProgressCallback = { url, currentBytes, totalBytes ->
        print("\n(KonanProperties) Downloading dependency: $url (${currentBytes}/${totalBytes}). ")
    }).download(url, tempLocation)
}

private fun hostOs(): String {
    val javaOsName = System.getProperty("os.name")
    return when {
        javaOsName == "Mac OS X" -> "macos"
        javaOsName == "Linux" -> "linux"
        javaOsName.startsWith("Windows") -> "windows"
        else -> throw TargetSupportException("Unknown operating system: $javaOsName")
    }
}

private fun hostArch(): String =
    when (val arch = System.getProperty("os.arch")) {
        "x86_64" -> "x86_64"
        "amd64" -> "x86_64"
        "arm64" -> "aarch64"
        "aarch64" -> "aarch64"
        else -> throw TargetSupportException("Unknown hardware platform: $arch")
    }

