package org.openasr.idiolect.presentation.toolwindow.audio

import org.openasr.idiolect.settings.IdiolectConfig
import org.openasr.idiolect.utils.AudioUtils
import java.awt.ItemSelectable
import java.awt.event.ItemEvent
import java.awt.event.ItemListener
import javax.sound.sampled.Mixer
import javax.swing.BoxLayout
import javax.swing.ButtonGroup
import javax.swing.JPanel
import javax.swing.JRadioButton

class AudioInputSelector : JPanel(), ItemSelectable {
    var selectedItem: Mixer.Info? = null
    private var itemListener: ItemListener? = null
    private var buttonGroup = ButtonGroup()

    init {
        layout = BoxLayout(this, BoxLayout.Y_AXIS)

        // Add initial list of available input devices
        addAllAudioInputDevices()

        // Listen for changes in available input devices
//        AudioSystem.getMixerInfo().forEach { info ->
//            AudioSystem.getMixer(info).addLineListener { event ->
//                if (event.type == javax.sound.sampled.LineEvent.Type.OPEN ||
//                    event.type == javax.sound.sampled.LineEvent.Type.CLOSE) {
//                    updateInputDeviceRadioButtons()
//                }
//            }
//        }
    }

    override fun addItemListener(listener: ItemListener) {
        itemListener = listener
    }

    override fun removeItemListener(l: ItemListener?) {
        itemListener = null
    }

    override fun getSelectedObjects(): Array<Mixer.Info?> {
        return arrayOf(selectedItem)
    }

    fun refresh() {
        reset()
        addAllAudioInputDevices()
    }

    private fun reset() {
        buttonGroup.clearSelection()
        selectedItem = null
        removeAll()
    }

    private fun addAllAudioInputDevices() {
        AudioUtils.getAudioInputDevices().forEach { info ->
            addRadioButton(info)
        }
    }

    private fun addRadioButton(info: Mixer.Info) {
        val radioButton = JRadioButton(info.name)
        radioButton.addActionListener {
            selectedItem?.let {
                itemListener?.itemStateChanged(ItemEvent(this, ItemEvent.ITEM_STATE_CHANGED, it, ItemEvent.DESELECTED))
            }

            selectedItem = info
            itemListener?.itemStateChanged(ItemEvent(this, ItemEvent.ITEM_STATE_CHANGED, info, ItemEvent.SELECTED))
        }

        if (info.name == IdiolectConfig.settings.audioInputDevice) {
            selectedItem = info
            radioButton.isSelected = true
        }

        buttonGroup.add(radioButton)
        add(radioButton)
        revalidate()
    }

    private fun removeRadioButton(info: Mixer.Info) {
        buttonGroup.elements.toList().filterIsInstance<JRadioButton>().find { it.text == info.name }?.let {
            buttonGroup.remove(it)
            remove(it)
            revalidate()
        }
    }
}
