package org.openasr.idiolect.utils

import org.assertj.core.internal.InputStreamsException
import org.openasr.idiolect.recognizer.CustomMicrophone
import java.io.*
import javax.sound.sampled.AudioInputStream
import javax.sound.sampled.AudioSystem
import javax.sound.sampled.AudioFileFormat.Type.WAVE
import javax.sound.sampled.AudioFormat

class TestAudioUtils {
    companion object {
        private val fileStoragePath = "src/test/resources/"

        fun getInputStream(name: String): AudioInputStream {
            val fileName = fixFileName(name)
            val url = CustomMicrophone::class.java.classLoader.getResource(fileName)
            if (url == null) {
                throw InputStreamsException("Could not find $fileStoragePath$fileName")
            }

            val inputStream = AudioSystem.getAudioInputStream(url)
            return inputStream
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

        private fun fixFileName(name: String) = if (name.endsWith(".wav")) name else name + ".wav"
    }
}
