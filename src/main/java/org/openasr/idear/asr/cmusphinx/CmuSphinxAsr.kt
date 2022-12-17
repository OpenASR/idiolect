package org.openasr.idear.asr.cmusphinx

import com.intellij.openapi.diagnostic.Logger
import org.openasr.idear.asr.AsrProvider

object CmuSphinxAsr : AsrProvider {
    private val logger = Logger.getInstance(javaClass)

    private lateinit var recognizer: CustomLiveSpeechRecognizer

    override fun activate() {
        recognizer = CustomLiveSpeechRecognizer
    }


    override fun displayName() = "CMU Sphinx"

    override fun waitForUtterance(): String {
        val result = recognizer.result

        println("Recognized: ")
        println("\tTop H:       " +
                result.result + " / " +
                result.result.bestToken + " / " +
                result.result.bestPronunciationResult)
        println("\tTop 3H:      " + result.getNbest(3))

        logger.info("Recognized:    ")
        logger.info("\tTop H:       " +
                result.result + " / " +
                result.result.bestToken + " / " +
                result.result.bestPronunciationResult)
        logger.info("\tTop 3H:      " + result.getNbest(3))

        return result.hypothesis
    }

    override fun startRecognition() = recognizer.startRecognition()

    override fun stopRecognition() = recognizer.stopRecognition()
}
