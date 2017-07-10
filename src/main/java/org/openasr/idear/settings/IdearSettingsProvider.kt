package org.openasr.idear.settings

import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.ServiceManager.getService
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import org.openasr.idear.settings.RecognitionServiceId.CMU_SPHINX
import org.openasr.idear.settings.TtsServiceId.MARY


@State(name = "IdearSettingsProvider", storages = arrayOf(Storage("recognition.xml")))
class IdearSettingsProvider : PersistentStateComponent<IdearSettingsProvider.State> {
  override fun getState() = state
  private var state: State = State()

  data class State(
      var recognitionService: RecognitionServiceId = CMU_SPHINX,
      var ttsService: TtsServiceId = MARY
  )

  override fun loadState(state: State) {
    this.state = state
  }

  companion object {
    val instance: IdearSettingsProvider
      get() = getService(IdearSettingsProvider::class.java)
  }
}
