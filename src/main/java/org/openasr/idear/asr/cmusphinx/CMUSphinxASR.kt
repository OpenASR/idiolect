package org.openasr.idear.asr.cmusphinx

import org.openasr.idear.asr.ASRProvider
import edu.cmu.sphinx.api.Configuration
import java.io.IOException
import java.util.logging.Level
import java.util.logging.Logger

class CMUSphinxASR : ASRProvider {
    private lateinit var recognizer: CustomLiveSpeechRecognizer

    init {
        val configuration = Configuration()
        configuration.acousticModelPath = ACOUSTIC_MODEL
        configuration.dictionaryPath = DICTIONARY_PATH
        configuration.grammarPath = GRAMMAR_PATH
        configuration.useGrammar = true
        configuration.grammarName = "command"

        try {
            recognizer = CustomLiveSpeechRecognizer(configuration)
        } catch (e: IOException) {
            logger.log(Level.SEVERE, "Couldn't initialize speech recognizer:", e)
        }
    }

    override fun waitForUtterance(): String {
        val result = recognizer.result

        println("Recognized: ")
        println("\tTop H:       " + result.result + " / " + result.result.bestToken + " / " + result.result.bestPronunciationResult)
        println("\tTop 3H:      " + result.getNbest(3))

        logger.info("Recognized:    ")
        logger.info("\tTop H:       " + result.result + " / " + result.result.bestToken + " / " + result.result.bestPronunciationResult)
        logger.info("\tTop 3H:      " + result.getNbest(3))

        return result.hypothesis
    }

    override fun startRecognition() = recognizer.startRecognition()

    override fun stopRecognition() = recognizer.stopRecognition()

    companion object {
        val MASTER_GAIN = 0.85
        val CONFIDENCE_LEVEL_THRESHOLD = 0.5

        private val ACOUSTIC_MODEL = "resource:/edu.cmu.sphinx.models.en-us/en-us"
        private val DICTIONARY_PATH = "resource:/edu.cmu.sphinx.models.en-us/cmudict-en-us.dict"
        private val GRAMMAR_PATH = "resource:/org.openasr.idear/grammars"

        private val logger = Logger.getLogger(CMUSphinxASR::class.java.simpleName)
    }
}