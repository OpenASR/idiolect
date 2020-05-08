package org.openasr.idear.asr.picovoice

import com.intellij.openapi.diagnostic.Logger
import org.openasr.idear.asr.ASRSystem
import org.openasr.idear.asr.cmusphinx.CustomLiveSpeechRecognizer
import org.openasr.idear.asr.picovoice.rhino.AudioRecorder
import org.openasr.idear.asr.picovoice.rhino.RhinoCallback
import org.openasr.idear.asr.picovoice.rhino.RhinoIntent
import org.openasr.idear.asr.picovoice.rhino.RhinoAudioConsumer

class PicovoiceASRSystem : ASRSystem, RhinoCallback {
    private val logger = Logger.getInstance(javaClass)

    // TODO: need to provide actual files, probably configurable
    companion object {
        const val MODEL_FILE_PATH = "TODO"
        const val CONTEXT_FILE_PATH = "TODO"
    }

    private var recorder: AudioRecorder? = null
    private var audioConsumer: RhinoAudioConsumer? = null

    constructor(modelFilePath: String, contextFilePath: String) {
        audioConsumer = RhinoAudioConsumer(modelFilePath, contextFilePath, this)
//        audioConsumer = CheetahAudioConsumer(this)
        recorder = AudioRecorder(audioConsumer)
    }

    override fun onIntentResult(isUnderstood: Boolean, intent: RhinoIntent?) {
//        intent.intent // String
//        intent.slots // Map<String, String>
        logger.info("onIntentResult: $isUnderstood, $intent");
        // TODO: create a class to map RhinoIntent to IDEService
//        IdeActions.ACTION_MOVE_LINE_UP_ACTION
//        IDEService.invokeAction()
    }

    override fun start() {
        logger.info("PicovoiceRecogniser started");
        startRecognition()
    }

    override fun waitForUtterance(): String {
        TODO("Not yet implemented")
    }

    /**
     * Starts recognition process.
     * @see CustomLiveSpeechRecognizer.stopRecognition
     */
    override fun startRecognition() {
        recorder?.start()
    }

    override fun stopRecognition() {
        recorder?.stop()
//        audioConsumer?.reset()
    }

    override fun terminate() {
        recorder?.stop()
        audioConsumer?.delete()
    }
}