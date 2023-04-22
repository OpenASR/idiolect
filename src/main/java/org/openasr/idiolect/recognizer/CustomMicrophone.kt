package org.openasr.idiolect.recognizer

import com.intellij.openapi.Disposable
import com.intellij.openapi.components.Service
import com.intellij.openapi.diagnostic.logger
import org.openasr.idiolect.settings.IdiolectConfig
import org.openasr.idiolect.utils.AudioUtils
import java.io.*
import java.util.concurrent.atomic.AtomicInteger
import javax.sound.sampled.*
import javax.sound.sampled.AudioFileFormat.Type.WAVE

@Service
class CustomMicrophone : Closeable, Disposable {
    companion object {
        private val log = logger<CustomMicrophone>()

        val DEFAULT_GAIN = 5
        val DEFAULT_NOISE = 10

        private const val sampleRate = 16000f
        private const val sampleSize = 16
        private const val bigEndian = false

        private val TEMP_FILE = IdiolectConfig.idiolectHomePath + "/temp.wav"
    }

    private val activeThreadCount = AtomicInteger(0)
    private var line: TargetDataLine? = null
    lateinit var stream: AudioInputStream
    private var isRecording: Boolean = false

    val format = AudioFormat(AudioFormat.Encoding.PCM_SIGNED, sampleRate, sampleSize, 1, 2, sampleRate, bigEndian)

    fun open() {
        if (line == null) {
            useDefaultLine()
        }
    }

    fun useDefaultLine() {
        log.info("Microphone using default line")
        useLine(AudioSystem.getTargetDataLine(format))
    }

    fun useInputDevice(device: Mixer.Info): TargetDataLine? {
        log.info("Microphone using device: ${device.name}")
        useLine(AudioSystem.getTargetDataLine(format, device))
        return line
    }

    fun useInputDevice(deviceName: String) {
        if (deviceName.isNotEmpty()) {
            val device = AudioUtils.getAudioInputDevices().firstOrNull { device ->
                device.name == deviceName
            }

            if (device != null) {
                log.info("Microphone using: ${deviceName}")
                useInputDevice(device)
                return
            }

            log.info("Microphone could not find '${deviceName}' not found")
        }

        useDefaultLine()
    }

    fun getLine() = line

    /** Acquire audio resources, but do not begin the flow of data */
    private fun useLine(line: TargetDataLine) {
        if (this.line != null) {
            log.warn("call to useLine: $line")
            log.warn("but already have a line: " + this.line)
            close()
        }
        line.open()
        log.info("Microphone line open")

//        if (line.isControlSupported(MASTER_GAIN))
//            log.info("Microphone: MASTER_GAIN supported")
//        else log.info("Microphone: MASTER_GAIN NOT supported")

        //masterGainControl = findMGControl(line);

        stream = AudioInputStreamWithAdjustableGain(line)
//        stream = AudioInputStream(line)
        this.line = line
    }

    /** Note: once a `line` is closed, it can not be reopened */
    override fun close() = dispose()

    override fun dispose() {
        line?.apply {
            stop()
            drain()
            stream.close()
            close()
            log.info("Microphone line closed")
            line = null
        }
    }

    /** Begin the flow of data for listening */
    @Synchronized
    fun startRecording(): Boolean {
        if (activeThreadCount.getAndIncrement() == 0) {
            line?.start().also { isRecording = true }
            log.info("Microphone started")
            return true
        }

        return false
    }

    @Synchronized
    fun stopRecording(): Boolean {
        if (activeThreadCount.decrementAndGet() == 0) {
            line?.stop().also { isRecording = false }
            log.info("Microphone stopped")
            return true
        }

        return false
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
