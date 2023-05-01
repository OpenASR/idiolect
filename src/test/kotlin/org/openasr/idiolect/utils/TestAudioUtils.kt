package org.openasr.idiolect.utils

import org.openasr.idiolect.recognizer.CustomMicrophone
import java.io.*
import javax.sound.sampled.AudioInputStream
import javax.sound.sampled.AudioSystem
import javax.sound.sampled.AudioFileFormat.Type.WAVE
import javax.sound.sampled.AudioFormat

class TestAudioUtils {
    companion object {
        private const val fileStoragePath = "src/test/resources/"

        fun recordAudioFile(name: String, duration: Long): File {
            val mic = CustomMicrophone()
            mic.useDefaultLine()
            val audioFilePath = fileStoragePath + fixFileName(name)
            println("Recording to $audioFilePath...")
            val file = mic.recordFromMic(audioFilePath, duration)
            println("end of recording")
            return file
        }

        fun getInputStream(name: String): AudioInputStream {
            val fileName = fixFileName(name)
            val url = CustomMicrophone::class.java.classLoader.getResource(fileName)
                ?: throw IOException("Could not find $fileStoragePath$fileName")

            return AudioSystem.getAudioInputStream(url)
        }

        fun saveAudioStream(audioOutputStream: ByteArrayOutputStream, format: AudioFormat, name: String) {
            val inputStream = ByteArrayInputStream(audioOutputStream.toByteArray())
            saveAudioStream(inputStream, format, audioOutputStream.size().toLong(), name)
        }

        fun saveAudioStream(inputStream: InputStream, format: AudioFormat, length: Long, name: String) {
            val audioInputStream = AudioInputStream(inputStream, format, length)
            saveAudioFile(audioInputStream, name)
        }

        private fun saveAudioFile(audioInputStream: AudioInputStream, name: String) {
            val fileName = fileStoragePath + fixFileName(name)
            val file = File(fileName)
            AudioSystem.write(audioInputStream, WAVE, file)
        }

        private fun fixFileName(name: String) = if (name.endsWith(".wav")) name else "$name.wav"
    }
}
