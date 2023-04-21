package org.openasr.idiolect.presentation

import com.intellij.openapi.components.service
import com.intellij.openapi.ui.ComboBox
import com.intellij.ui.SimpleListCellRenderer
import com.intellij.ui.dsl.builder.panel
import org.openasr.idiolect.recognizer.CustomMicrophone
import java.awt.event.ItemEvent
import java.util.*
import javax.swing.JComponent
import javax.sound.sampled.AudioSystem
import javax.sound.sampled.LineEvent
import javax.sound.sampled.LineListener
import javax.sound.sampled.Mixer
import javax.sound.sampled.TargetDataLine
import javax.swing.DefaultComboBoxModel

class AudioTab {
    private var microphone: CustomMicrophone = service()
    private var vuMeter = VuMeter(null)

    fun createComponent(): JComponent {
        val audioInputDevices = getAudioInputDevices()
        val audioInputSelector = ComboBox(DefaultComboBoxModel(Vector(audioInputDevices)))
        audioInputSelector.renderer = SimpleListCellRenderer.create("No input devices found") {
            it.name
        }

        // add event handler
        audioInputSelector.addItemListener { event ->
            val selectedDevice = audioInputSelector.selectedItem as Mixer.Info

            if (event.stateChange == ItemEvent.SELECTED) {
                val line = microphone.useInputDevice(selectedDevice)
                startVuMeter(line)
            } else { // if (event.stateChange == ItemEvent.DESELECTED) {
                stopVuMeter()
            }
        }

        return panel {
            row("Input device") { cell(audioInputSelector) }
            row { cell(vuMeter) }
        }
    }

    private fun addMixerInfoListener() {
        val listener = object : LineListener {
            override fun update(event: LineEvent) {
                if (event.type == LineEvent.Type.OPEN) {
                    val audioInputDevices = getAudioInputDevices()

                    // Update the combo box with the new list of audio input devices
//                    comboBox.removeAllItems()
//                    audioInputDevices.forEach { comboBox.addItem(it) }
                }
            }
        }

        val mixers = AudioSystem.getMixerInfo()

        for (mixerInfo in mixers) {
            val mixer = AudioSystem.getMixer(mixerInfo)
            mixer.addLineListener(listener)
        }
    }

    private fun getAudioInputDevices(): List<Mixer.Info> {
        val mixerInfoArray: Array<Mixer.Info> = AudioSystem.getMixerInfo()
        return mixerInfoArray.filter { mixerInfo ->
            val mixer = AudioSystem.getMixer(mixerInfo)

            val targetLineInfo = mixer.targetLineInfo
            targetLineInfo.any { it.lineClass == TargetDataLine::class.java }
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
}
