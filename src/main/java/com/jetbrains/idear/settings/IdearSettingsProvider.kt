package com.jetbrains.idear.settings

import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage


@State(name = "IdearSettingsProvider", storages = arrayOf(Storage("recognition.xml")))
class IdearSettingsProvider : PersistentStateComponent<IdearSettingsProvider.State> {
    private val state = State();

    class State {
        var recognitionService: RecognitionServiceId = RecognitionServiceId.CMU_SPHINX
        var ttsService: TtsServiceId = TtsServiceId.MARY
    }

    override fun getState(): State = state

    override fun loadState(state: State) {
        this.state.recognitionService = state.recognitionService
        this.state.ttsService = state.ttsService
    }

    companion object {
        fun getInstance(): IdearSettingsProvider {
            return ServiceManager.getService(IdearSettingsProvider::class.java)
        }
    }
}
