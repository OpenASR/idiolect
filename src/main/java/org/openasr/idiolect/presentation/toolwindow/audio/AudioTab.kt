package org.openasr.idiolect.presentation.toolwindow.audio

import com.intellij.openapi.Disposable
import com.intellij.openapi.components.service
import com.intellij.openapi.diagnostic.logger
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
import javax.swing.JButton
import javax.swing.JComponent
import javax.swing.JLabel
import javax.swing.JSlider
import javax.swing.event.AncestorEvent
import javax.swing.event.AncestorListener
import kotlin.concurrent.thread


class AudioTab : JComponent(), Disposable, AncestorListener {
    private val log = logger<AudioTab>()
    private var microphone: CustomMicrophone = service()
    private var vuMeter = VuMeter(microphone, VuMeter.RMS_MODE)
    private val startTestButton = JButton("Start test")
    private val replayButton = JButton("Replay")
    private val waveformVisualizer = WaveformVisualizer(microphone)
    private val waveformButton = JButton("Start")
    private var isTabVisible = false
    private var isRecordingTestClip = false
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
                microphone.useInputDevice(selectedDevice)
                IdiolectConfig.settings.audioInputDevice = selectedDevice.name
                startThreads()
                startWaveform()
            } else { // if (event.stateChange == ItemEvent.DESELECTED) {
                stopThreads()
                microphone.close()
            }
        }

        return audioInputSelector
    }

    /** Check to see if this tab has just become visible */
    override fun ancestorAdded(event: AncestorEvent?) {
        if (!isTabVisible && isVisible) {
            isTabVisible = true
//            log.debug("Audio tab visible, starting")
            startThreads()
        }
    }

    /** Check to see if this tab is still visible */
    override fun ancestorRemoved(event: AncestorEvent?) {
        if (isTabVisible) {
            isTabVisible = false
//            log.debug("Audio tab not visible, stopping")
            stopThreads()
        }
    }

    override fun ancestorMoved(event: AncestorEvent?) {}

    override fun dispose() {
        vuMeter.dispose()
        waveformVisualizer.dispose()
    }

    private fun startThreads() {
        microphone.startRecording()
        startVuMeter()
    }

    private fun stopThreads() {
        microphone.stopRecording()
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
            recordClip(!isRecordingTestClip)
        }

        replayButton.addActionListener {
            clip?.apply {
                this.framePosition = 0
                start()
            }
        }
    }

    private fun recordClip(recordClip: Boolean) {
        if (recordClip) {
            startTestButton.text = "Stop"
            replayButton.isEnabled = false

            // Start recording audio data to ByteArrayOutputStream
            thread(name = "Audio Clip Recorder") {
                val targetDataLine = microphone.getLine()!!
                val stream = microphone.stream
                val byteArrayOutputStream = ByteArrayOutputStream()

                // A larger buffer size can result in smoother audio playback but may also introduce latency.
                // If the buffer size is too small, it can result in choppy playback or gaps in the audio.
                val bufferSize = targetDataLine.bufferSize // 4
                val data = ByteArray(bufferSize)
                while (!replayButton.isEnabled) {
                    val numBytesRead = microphone.read(data, data.size)
                    if (numBytesRead == -1) {
                        recordClip(false)
                    } else {
                        byteArrayOutputStream.write(data, 0, numBytesRead)
                    }
                }

                val clip = AudioSystem.getClip()
                val bytes = byteArrayOutputStream.toByteArray()
                clip.open(CustomMicrophone.format, bytes, 0, bytes.size)
                this.clip = clip
            }
        } else {
            startTestButton.text = "Record clip"
            replayButton.isEnabled = true
        }
        isRecordingTestClip = recordClip
    }

    private fun initialiseWaveformButton() {
        waveformButton.addActionListener {
            if (waveformVisualizer.isRunning()) {
//                log.debug("Clicked to stop waveform")
                stopWaveform()
            } else {
//                log.debug("Clicked to stop waveform")
                startWaveform()
            }
        }
    }

    private fun startVuMeter() {
        vuMeter.start()
    }

    private fun stopVuMeter() {
        vuMeter.stop()
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
