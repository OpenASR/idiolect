package org.openasr.idiolect.windows

import com.sun.jna.Native
import org.junit.Test
import org.vosk.LibVosk
import java.io.File
import java.io.IOException
import java.nio.file.Files
import java.nio.file.StandardCopyOption

class WindowsTest {
  @Test
  fun shouldRegisterDlls () {
    // To get a tmp folder we unpack small library and mark it for deletion
    val tmpFile = Native.extractFromResourcePath("/win32-x86-64/empty")
    val tmpDir = tmpFile.parentFile
    File(tmpDir, tmpFile.name + ".x").createNewFile()

    // Now unpack dependencies
    unpackDll(tmpDir, "libwinpthread-1")
    unpackDll(tmpDir, "libgcc_s_seh-1")
    unpackDll(tmpDir, "libstdc++-6")

    Native.register(this.javaClass, "libvosk")
  }

  @Throws(IOException::class)
  private fun unpackDll(targetDir: File, lib: String) {
    val source = LibVosk::class.java.getResourceAsStream("/win32-x86-64/$lib.dll")
    Files.copy(source, File(targetDir, "$lib.dll").toPath(), StandardCopyOption.REPLACE_EXISTING)
  }
}
