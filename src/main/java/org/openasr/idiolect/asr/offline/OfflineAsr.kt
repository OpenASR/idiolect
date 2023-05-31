package org.openasr.idiolect.asr.offline

import com.intellij.openapi.components.service
import com.intellij.openapi.options.Configurable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.openasr.idiolect.asr.AsrProvider
import org.openasr.idiolect.asr.models.ModelManager
import org.openasr.idiolect.recognizer.CustomMicrophone

abstract class OfflineAsr<C : Configurable>(internal var modelManager: ModelManager<C>) : AsrProvider {
    protected lateinit var microphone: CustomMicrophone
    protected var listening = false

    override fun activate() {
        modelManager.initialiseModel(
            """
                <p>Download and configure the path to your ${displayName()} speech model.<p>
                <p><a href="${modelManager.modelsPageUrl}">${modelManager.modelsPageUrl}</a></p>
            """.trimIndent()
        ) { model ->
            CoroutineScope(Dispatchers.IO).launch {
                setModel(model)
            }
        }

        microphone = service()
        microphone.open()
    }

    override fun deactivate() = microphone.close()

    /**
     * Starts recognition process.
     */
    override fun startRecognition(): Boolean {
        listening = true
        return microphone.startRecording()
    }

    /**
     * Stops recognition process.
     * Recognition process is paused until the next call to startRecognition.
     */
    override fun stopRecognition(): Boolean {
        listening = false
        return microphone.stopRecording()
    }
}
