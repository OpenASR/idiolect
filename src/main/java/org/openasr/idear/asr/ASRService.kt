package org.openasr.idear.asr

import edu.cmu.sphinx.api.Configuration
import org.openasr.idear.recognizer.CustomLiveSpeechRecognizer
import java.io.IOException
import java.util.logging.Level
import java.util.logging.Logger

class ASRService {
    private lateinit var speechThread: Thread
    private lateinit var recognizer: CustomLiveSpeechRecognizer

    fun init() {
        val configuration = Configuration()
        configuration.acousticModelPath = ACOUSTIC_MODEL
        configuration.dictionaryPath = DICTIONARY_PATH
        configuration.grammarPath = GRAMMAR_PATH
        configuration.useGrammar = true
        configuration.grammarName = "command"

        try {
            recognizer = CustomLiveSpeechRecognizer(configuration)
            //            recognizer.setMasterGain(MASTER_GAIN);
            speechThread = Thread(ASRControlLoop(recognizer), "ASR Thread")
            recognizer.startRecognition(true)
            // Fire up control-loop
            speechThread.start()
        } catch (e: IOException) {
            logger.log(Level.SEVERE, "Couldn't initialize speech recognizer:", e)
        }
    }

    fun activate(): Boolean {
        //        if (getStatus() == Status.INIT) {
        //            // Cold start prune cache
        //            recognizer.startRecognition(true);
        //        }

        return ListeningState.activate()
    }

    fun deactivate(): Boolean {
        return ListeningState.standBy()
    }

    fun dispose() {
        // Deactivate in the first place, therefore actually
        // prevent activation upon the user-input
        deactivate()
        terminate()
    }

    private fun terminate() = recognizer.stopRecognition()

    companion object {
        val MASTER_GAIN = 0.85
        val CONFIDENCE_LEVEL_THRESHOLD = 0.5

        private val ACOUSTIC_MODEL = "resource:/edu.cmu.sphinx.models.en-us/en-us"
        private val DICTIONARY_PATH = "resource:/edu.cmu.sphinx.models.en-us/cmudict-en-us.dict"
        private val GRAMMAR_PATH = "resource:/org.openasr.idear/grammars"

        private val logger = Logger.getLogger(ASRService::class.java.simpleName)
    }
}

// This is for testing purposes solely
fun main(args: Array<String>) {
    val asrService = ASRService()
    asrService.init()
    ListeningState.activate()
}
