package org.jetbrains.kotlin.konan.library.impl

import org.jetbrains.kotlin.konan.file.File
import org.jetbrains.kotlin.konan.library.*
import org.jetbrains.kotlin.konan.target.KonanTarget
import org.jetbrains.kotlin.library.*
import org.jetbrains.kotlin.library.impl.*
import java.nio.file.FileSystem

open class TargetedLibraryLayoutImpl(klib: File, component: String, override val target: KonanTarget?) :
    KotlinLibraryLayoutImpl(klib, component), TargetedKotlinLibraryLayout {

    override val extractingToTemp: TargetedKotlinLibraryLayout by lazy {
        ExtractingTargetedLibraryImpl(this)
    }

    override fun directlyFromZip(zipFileSystem: FileSystem): TargetedKotlinLibraryLayout =
        FromZipTargetedLibraryImpl(this, zipFileSystem)

}

class BitcodeLibraryLayoutImpl(klib: File, component: String, target: KonanTarget?) :
    TargetedLibraryLayoutImpl(klib, component, target), BitcodeKotlinLibraryLayout {
    override val extractingToTemp: BitcodeKotlinLibraryLayout by lazy {
        ExtractingBitcodeLibraryImpl(this)
    }

    override fun directlyFromZip(zipFileSystem: FileSystem): BitcodeKotlinLibraryLayout =
        FromZipBitcodeLibraryImpl(this, zipFileSystem)

}

class SwiftExtendedLibraryLayoutImpl(klib: File, component: String) : KotlinLibraryLayoutImpl(klib, component), SwiftExtendedLibraryLayout {
    override val extractingToTemp: SwiftExtendedLibraryLayout by lazy {
        ExtractingSwiftExtendedLibrary(this)
    }

    override fun directlyFromZip(zipFileSystem: FileSystem): KotlinLibraryLayout =
        FromZipSwiftExtendedLibraryLayoutImpl(this, zipFileSystem)
}

open class TargetedLibraryAccess<L : KotlinLibraryLayout>(klib: File, component: String, val target: KonanTarget?) :
    BaseLibraryAccess<L>(klib, component) {
    override val layout = TargetedLibraryLayoutImpl(klib, component, target)
}

open class BitcodeLibraryAccess<L : KotlinLibraryLayout>(klib: File, component: String, target: KonanTarget?) :
    TargetedLibraryAccess<L>(klib, component, target) {
    override val layout = BitcodeLibraryLayoutImpl(klib, component, target)
}

open class SwiftExtendedLibraryAccess<L : KotlinLibraryLayout>(klib: File, component: String) :
    BaseLibraryAccess<L>(klib, component) {
    override val layout = SwiftExtendedLibraryLayoutImpl(klib, component)
}

private open class FromZipTargetedLibraryImpl(zipped: TargetedLibraryLayoutImpl, zipFileSystem: FileSystem) :
    FromZipBaseLibraryImpl(zipped, zipFileSystem), TargetedKotlinLibraryLayout

private class FromZipBitcodeLibraryImpl(zipped: BitcodeLibraryLayoutImpl, zipFileSystem: FileSystem) :
    FromZipTargetedLibraryImpl(zipped, zipFileSystem), BitcodeKotlinLibraryLayout

private class FromZipSwiftExtendedLibraryLayoutImpl(zipped: SwiftExtendedLibraryLayoutImpl, zipFileSystem: FileSystem) :
    FromZipBaseLibraryImpl(zipped, zipFileSystem), SwiftExtendedLibraryLayout

open class ExtractingTargetedLibraryImpl(zipped: TargetedLibraryLayoutImpl) :
    ExtractingKotlinLibraryLayout(zipped),
    TargetedKotlinLibraryLayout {

    override val includedDir: File by lazy { zipped.extractDir(zipped.includedDir) }
}

class ExtractingBitcodeLibraryImpl(zipped: BitcodeLibraryLayoutImpl) :
    ExtractingTargetedLibraryImpl(zipped), BitcodeKotlinLibraryLayout {

    override val kotlinDir: File by lazy { zipped.extractDir(zipped.kotlinDir) }
    override val nativeDir: File by lazy { zipped.extractDir(zipped.nativeDir) }
}

open class ExtractingSwiftExtendedLibrary(zipped: SwiftExtendedLibraryLayoutImpl) :
    ExtractingKotlinLibraryLayout(zipped), SwiftExtendedLibraryLayout {
    override val swiftSourcesDir: File by lazy { zipped.extractDir(zipped.swiftSourcesDir) }
    override val objcHeadersDir: File by lazy { zipped.extractDir(zipped.objcHeadersDir) }
}
