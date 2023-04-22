package org.openasr.idiolect.presentation

import com.intellij.openapi.Disposable
import com.intellij.openapi.components.service
import com.intellij.ui.dsl.builder.panel
import com.intellij.ui.util.preferredWidth
import org.openasr.idiolect.recognizer.CustomMicrophone
import org.openasr.idiolect.settings.IdiolectConfig
import java.awt.BorderLayout
import java.awt.event.ItemEvent
import java.io.ByteArrayOutputStream
import javax.sound.sampled.AudioSystem
import javax.sound.sampled.Clip
import javax.sound.sampled.Mixer
import javax.sound.sampled.TargetDataLine
import javax.swing.JButton
import javax.swing.JComponent
import javax.swing.JLabel
import javax.swing.JSlider
import javax.swing.event.AncestorEvent
import javax.swing.event.AncestorListener
import kotlin.concurrent.thread


class AudioTab : JComponent(), Disposable, AncestorListener {
    private var vuMeter = VuMeter(null, VuMeter.RMS_MODE)
    private var microphone: CustomMicrophone = service()
    private val startTestButton = JButton("Start test")
    private val replayButton = JButton("Replay")
    private val waveformVisualizer = WaveformVisualizer()
    private val waveformButton = JButton("Start")
    private var isTabVisible = false
    private var isRecording = false
    private var clip: Clip? = null

    init {
        layout = BorderLayout()
        add(createComponent(), BorderLayout.CENTER)
        addAncestorListener(this)
    }

    private fun createComponent(): JComponent {
        val audioInputSelector = createAudioInputSelector()

        // Button to detect bluetooth/USB microphones
        val refreshButton = JButton("Refresh devices")
        refreshButton.addActionListener {
            audioInputSelector.refresh()
        }

        initialiseTestButtons()

        val volumeSlider = createVolumeSlider()
        val noiseLevelSlider = createNoiseLevelSlider()

        initialiseWaveformButton()

        return panel {
            threeColumnsRow({
                panel {
                    row {
                        cell(refreshButton)
                    }
                    row {
                        cell(audioInputSelector)
                    }

                    row { cell(vuMeter) }

                    row {
                        cell(startTestButton)
                        cell(replayButton)
                    }
                }
            }, {
                panel {
                    row {
                        label("Volume").applyToComponent {
                            horizontalAlignment = JLabel.CENTER
                            preferredWidth = 50
                        }
                        label("Noise").applyToComponent {
                            horizontalAlignment = JLabel.CENTER
                            preferredWidth = 50
                        }
                    }
                    row {
                        cell(volumeSlider).applyToComponent {
                            preferredWidth = 50
                        }
                        cell(noiseLevelSlider).applyToComponent {
                            preferredWidth = 50
                        }
                    }
                }
            }, {
                panel {
                    row {
                        cell(waveformVisualizer)
                    }
                    row {
                        cell(waveformButton)
                    }
                }
            })
        }
    }

    private fun createAudioInputSelector(): AudioInputSelector {
        val audioInputSelector = AudioInputSelector()

        // add event handler
        audioInputSelector.addItemListener { event ->
            val selectedDevice = audioInputSelector.selectedItem as Mixer.Info

            if (event.stateChange == ItemEvent.SELECTED) {
                val line = microphone.useInputDevice(selectedDevice)
                IdiolectConfig.settings.audioInputDevice = selectedDevice.name
                startVuMeter(line)
                startWaveform(line)
            } else { // if (event.stateChange == ItemEvent.DESELECTED) {
                stopThreads()
                microphone.stopRecording()
            }
        }

        return audioInputSelector
    }

    override fun ancestorAdded(event: AncestorEvent?) {
        if (!isTabVisible && isVisible) {
            isTabVisible = true
            startThreads()
        }
    }

    override fun ancestorRemoved(event: AncestorEvent?) {
        if (isTabVisible) {
            isTabVisible = false
            stopThreads()
        }
    }

    override fun ancestorMoved(event: AncestorEvent?) {}

    override fun dispose() {
        vuMeter.dispose()
        waveformVisualizer.dispose()
    }

    private fun startThreads() {
        vuMeter.start()
    }

    private fun stopThreads() {
        stopVuMeter()
        stopWaveform()
    }

    private fun createVolumeSlider(): JSlider {
        val volumeSlider = JSlider(JSlider.VERTICAL, 0, 10, IdiolectConfig.settings.audioGain)

        volumeSlider.addChangeListener {
            val volume = volumeSlider.value
            microphone.setVolume(volume)
            IdiolectConfig.settings.audioGain = volume
        }

        return volumeSlider
    }

    private fun createNoiseLevelSlider(): JSlider {
        val noiseLevelSlider = JSlider(JSlider.VERTICAL, 0, 512, IdiolectConfig.settings.audioNoise)

        noiseLevelSlider.addChangeListener {
            val noiseLevel = noiseLevelSlider.value
            microphone.setNoiseLevel(noiseLevel)
            IdiolectConfig.settings.audioNoise = noiseLevel
        }

        return noiseLevelSlider
    }

    /**
     * Buttons to allow user to hear what idiolect is hearing
     */
    private fun initialiseTestButtons() {
        replayButton.isEnabled = false
        startTestButton.addActionListener {
            if (!isRecording) {
//                microphone.startRecording()
                startTestButton.text = "Stop Test"
                replayButton.isEnabled = false

                // Start recording audio data to ByteArrayOutputStream
                thread(name = "Audio Clip Recorder") {
                    val targetDataLine = microphone.getLine()!!
                    val stream = microphone.stream
                    val byteArrayOutputStream = ByteArrayOutputStream()

                    // A larger buffer size can result in smoother audio playback but may also introduce latency.
                    // If the buffer size is too small, it can result in choppy playback or gaps in the audio.
                    val bufferSize = targetDataLine.bufferSize / 4
                    val data = ByteArray(bufferSize)
                    while (!replayButton.isEnabled) {
                        val numBytesRead = stream.read(data, 0, data.size)
                        byteArrayOutputStream.write(data, 0, numBytesRead)
                    }

                    val clip = AudioSystem.getClip()
                    val bytes = byteArrayOutputStream.toByteArray()
                    clip.open(microphone.format, bytes, 0, bytes.size)
                    this.clip = clip
                }
            } else {
                startTestButton.text = "Start test"
                replayButton.isEnabled = true
            }
            isRecording = !isRecording
        }

        replayButton.addActionListener {
            clip?.apply {
                this.framePosition = 0
                start()
            }
        }
    }

    private fun initialiseWaveformButton() {
        waveformButton.addActionListener {
            if (waveformVisualizer.isRunning()) {
                stopWaveform()
            } else {
                startWaveform()
            }
        }
    }

    private fun startVuMeter(line: TargetDataLine?) {
        if (line != null) {
            vuMeter.stop()
            vuMeter.setDataLine(line)
            vuMeter.setStream(microphone.stream)
            vuMeter.start()
        }
    }

    private fun stopVuMeter() {
        vuMeter.stop()
    }

    private fun startWaveform(line: TargetDataLine?) {
        if (line != null) {
            waveformVisualizer.stop()
            waveformVisualizer.setDataLine(line)
            waveformVisualizer.setStream(microphone.stream)
//            waveformVisualizer.setStream(FileInputStream(File(IdiolectConfig.idiolectHomePath + "/temp.wav")))
            startWaveform()
        }
    }

    private fun startWaveform() {
        waveformVisualizer.start()
        waveformButton.text = "Stop"
    }

    private fun stopWaveform() {
        waveformVisualizer.stop()
        waveformButton.text = "Start"
    }
}
