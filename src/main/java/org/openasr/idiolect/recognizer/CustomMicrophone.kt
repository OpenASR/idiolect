package org.openasr.idiolect.recognizer

import com.intellij.openapi.Disposable
import com.intellij.openapi.components.Service
import com.intellij.openapi.diagnostic.logger
import org.openasr.idiolect.settings.IdiolectConfig
import java.io.*
import javax.sound.sampled.*
import javax.sound.sampled.AudioFileFormat.Type.WAVE
import javax.sound.sampled.FloatControl.Type.MASTER_GAIN

@Service
class CustomMicrophone : Closeable, Disposable {
    companion object {
        private val log = logger<CustomMicrophone>()

        private const val sampleRate = 16000f
        private const val sampleSize = 16
        private const val bigEndian = false

        private val TEMP_FILE = IdiolectConfig.idiolectHomePath + "/temp.wav"
    }

    private var line: TargetDataLine? = null
    lateinit var stream: AudioInputStream
    private var isRecording: Boolean = false

    val format = AudioFormat(AudioFormat.Encoding.PCM_SIGNED, sampleRate, sampleSize, 1, 2, sampleRate, bigEndian)

    fun open() {
        if (line == null) {
            useLine(AudioSystem.getTargetDataLine(format))
        }
    }

    fun useInputDevice(device: Mixer.Info): TargetDataLine? {
        log.info("Using audio input device: ${device.name}")
        useLine(AudioSystem.getTargetDataLine(format, device))
        return line
    }

    fun getLine() = line

//    fun useInputDevice(device: String): TargetDataLine? {
//        log.info("Using audio input device: ${device}")
//        AudioSystem.
//        useLine(AudioSystem.getTargetDataLine(format, device))
//        return line
//    }

    private fun useLine(line: TargetDataLine) {
        line.open()

//        if (line.isControlSupported(MASTER_GAIN))
//            log.info("Microphone: MASTER_GAIN supported")
//        else log.info("Microphone: MASTER_GAIN NOT supported")

        //masterGainControl = findMGControl(line);

        stream = AudioInputStreamWithAdjustableGain(line)
//        stream = AudioInputStream(line)
        this.line = line
    }

    override fun close() = dispose()

    override fun dispose() {
        stopRecording()
        line?.close()
        stream.close()
        line = null
    }

    fun startRecording() {
        line?.start().also { isRecording = true }
    }
    fun stopRecording() {
        line?.stop().also { isRecording = false }
    }

    fun isRecording() = isRecording

    /** @param level 0 to 100 */
    fun setNoiseLevel(level: Int) {
        (stream as AudioInputStreamWithAdjustableGain).setNoiseLevel(level.toDouble())
    }

    /** @param volume 0 to 100 */
    fun setVolume(volume: Int) {
        (stream as AudioInputStreamWithAdjustableGain).setMasterGain(volume.toDouble())

//        val mixer = AudioSystem.getMixer(null) as Mixer
//        val info = mixer.mixerInfo
//        val ctl = mixer.getControl(MASTER_GAIN) as FloatControl
//        val range = ctl.maximum - ctl.minimum
//        ctl.value = volume * range + ctl.minimum

//        val hMixer = WinMM.mixerOpen(null, 0, null, null, MIXER_OBJECTF_MIXER)
//        val mixerinfo = MIXERINFO()
//        mixerinfo.cbSize = MIXERINFO.SIZEOF
//        WinMM.mixerGetDevCaps(hMixer, 0, mixerinfo.size())
//        val hwnd: HWND? = null
//        val mixervolume = MIXERVOLUME()
//        mixervolume.cbStruct = MIXERVOLUME.SIZEOF
//        mixervolume.dwComponentType = MIXERLINE_COMPONENTTYPE_DST_SPEAKERS.toUInt()
//        val nChannels = mixerinfo.cDestinations
//        val volumes = ShortArray(nChannels)
//        volumes.fill((volume / 100.0 * 65535).toShort())
//        mixervolume.cChannels = nChannels.toUInt()
//        mixervolume.pChannels = volumes
//        WinMM.mixerSetControlDetails(hMixer, mixervolume.ptr, MIXER_SETCONTROLDETAILSF_VALUE)
//        WinMM.mixerClose(hMixer)
    }

    @Throws(IOException::class)
    fun recordFromMic(duration: Long): File {
        //Why is this in a thread?
        Thread {
            try {
                Thread.sleep(duration)
            } catch (ignored: InterruptedException) {
            } finally {
                stopRecording()
            }
        }.start()

        startRecording()

        return File(TEMP_FILE).apply {
            this.parentFile.mkdirs()
            AudioSystem.write(stream, WAVE, this)
        }
    }
}
