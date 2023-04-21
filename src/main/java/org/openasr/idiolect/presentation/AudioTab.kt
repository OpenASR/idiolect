package org.openasr.idiolect.presentation

import com.intellij.openapi.components.service
import com.intellij.ui.dsl.builder.panel
import com.intellij.ui.util.preferredWidth
import org.openasr.idiolect.recognizer.CustomMicrophone
import java.awt.event.ItemEvent
import java.io.ByteArrayOutputStream
import javax.sound.sampled.AudioSystem
import javax.sound.sampled.Clip
import javax.swing.JComponent
import javax.sound.sampled.Mixer
import javax.sound.sampled.TargetDataLine
import javax.swing.JButton
import javax.swing.JLabel
import javax.swing.JSlider
import kotlin.concurrent.thread

class AudioTab {
    private var vuMeter = VuMeter(null)
    private var microphone: CustomMicrophone = service()
    private val startTestButton = JButton("Start test")
    private val replayButton = JButton("Replay")
    private val waveformVisualizer = WaveformVisualizer()
    private var isRecording = false
    private var clip: Clip? = null

    fun createComponent(): JComponent {
        val audioInputSelector = AudioInputSelector()

        // add event handler
        audioInputSelector.addItemListener { event ->
            val selectedDevice = audioInputSelector.selectedItem as Mixer.Info

            if (event.stateChange == ItemEvent.SELECTED) {
                val line = microphone.useInputDevice(selectedDevice)
                startVuMeter(line)
                startWaveform(line)
            } else { // if (event.stateChange == ItemEvent.DESELECTED) {
                stopVuMeter()
                stopWaveform()
                microphone.stopRecording()
            }
        }

        // Button to detect bluetooth/USB microphones
        val refreshButton = JButton("Refresh devices")
        refreshButton.addActionListener {
            audioInputSelector.refresh()
        }

        initialiseTestButtons()

        val volumeSlider = createVolumeSlider()
        val noiseLevelSlider = createNoiseLevelSlider()

        val waveformButton = JButton("Start")
        waveformButton.addActionListener {
            if (waveformVisualizer.isRunning()) {
                waveformVisualizer.stop()
                waveformButton.text = "Start"
            } else {
                waveformVisualizer.start()
                waveformButton.text = "Stop"
            }
        }

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

    private fun createVolumeSlider(): JSlider {
        val volumeSlider = JSlider(JSlider.VERTICAL, 0, 100, 50)

        volumeSlider.addChangeListener {
            val volume = volumeSlider.value
            microphone.setVolume(volume)
        }

        return volumeSlider
    }

    private fun createNoiseLevelSlider(): JSlider {
        val noiseLevelSlider = JSlider(JSlider.VERTICAL, 0, 100, 10)

        noiseLevelSlider.addChangeListener {
            val noiseLevel = noiseLevelSlider.value
            microphone.setNoiseLevel(noiseLevel)
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
                thread {
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

    private fun startVuMeter(line: TargetDataLine?) {
        if (line != null) {
            vuMeter.stop()
            vuMeter.setDataLine(line)
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
            waveformVisualizer.start()
        }
    }

    private fun stopWaveform() {
        waveformVisualizer.stop()
    }
}
