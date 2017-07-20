package org.openasr.idear.asr.cmusphinx

import com.intellij.openapi.diagnostic.Logger
import edu.cmu.sphinx.api.Configuration
import org.openasr.idear.asr.ASRProvider

class CMUSphinxASR : ASRProvider {
    val MASTER_GAIN = 0.85
    val CONFIDENCE_LEVEL_THRESHOLD = 0.5

    private val ACOUSTIC_MODEL = "resource:/edu.cmu.sphinx.models.en-us/en-us"
    private val DICTIONARY_PATH = "resource:/edu.cmu.sphinx.models.en-us/cmudict-en-us.dict"
    private val GRAMMAR_PATH = "resource:/org.openasr.idear/grammars"

    private val logger = Logger.getInstance(CMUSphinxASR::class.java)

    private var recognizer = CustomLiveSpeechRecognizer(Configuration().apply {
        acousticModelPath = ACOUSTIC_MODEL
        dictionaryPath = DICTIONARY_PATH
        grammarPath = GRAMMAR_PATH
        useGrammar = true
        grammarName = "command"
    })

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

}