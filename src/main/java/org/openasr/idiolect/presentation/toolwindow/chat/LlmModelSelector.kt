package org.openasr.idiolect.presentation.toolwindow.chat

import com.intellij.openapi.Disposable
import com.intellij.openapi.application.invokeLater
import com.intellij.openapi.components.service
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.ui.ComboBox
import org.openasr.idiolect.nlp.ai.AiService
import org.openasr.idiolect.nlp.ai.OpenAiClient
import java.awt.event.ItemEvent
import java.awt.event.ItemListener
import javax.swing.DefaultComboBoxModel
import kotlin.reflect.KMutableProperty0

class LlmModelSelector(private val type: OpenAiClient.ModelType,
                       private val property: KMutableProperty0<String>)
    : ComboBox<String>(), ItemListener, Disposable, DumbAware
{
    private val aiService = service<AiService>()
    private val comboBoxModel = DefaultComboBoxModel<String>()

    init {
        setModel(comboBoxModel)
        addItemListener(this)

        invokeLater {
            update()
        }
    }

    override fun dispose() {
        removeItemListener(this)
    }

    override fun itemStateChanged(e: ItemEvent?) {
        if (e?.stateChange == ItemEvent.SELECTED && comboBoxModel.selectedItem is String) {
            property.set(comboBoxModel.selectedItem as String)
        }
    }
    //    override fun actionPerformed(e: ActionEvent?) {
//        super.actionPerformed(e)
//        property.set(comboBoxModel.selectedItem as String)
//    }

    fun update() {
        val models = aiService.listModels(type)
        comboBoxModel.removeAllElements()
        comboBoxModel.addAll(models)
        selectedItem = property.get()
    }
}
