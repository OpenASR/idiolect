package org.openasr.idear.asr.cmusphinx

import com.intellij.openapi.components.service
import com.intellij.openapi.diagnostic.Logger
import edu.cmu.sphinx.api.*
import edu.cmu.sphinx.decoder.ResultListener
import edu.cmu.sphinx.frontend.endpoint.SpeechClassifier.PROP_THRESHOLD
import edu.cmu.sphinx.frontend.util.StreamDataSource
import org.openasr.idear.recognizer.CustomMicrophone
import java.io.InputStream

/**
 * High-level class for live speech recognition.
 */

const val MASTER_GAIN = 0.85
const val CONFIDENCE_LEVEL_THRESHOLD = 0.5

private const val ACOUSTIC_MODEL = "resource:/edu.cmu.sphinx.models.en-us/en-us"
private const val DICTIONARY_PATH = "resource:/edu.cmu.sphinx.models.en-us/cmudict-en-us.dict"
private const val GRAMMAR_PATH = "resource:/org.openasr.idear/grammars"

val configuration = Configuration().apply {
    acousticModelPath = ACOUSTIC_MODEL
    dictionaryPath = DICTIONARY_PATH
    grammarPath = GRAMMAR_PATH
    useGrammar = true
    grammarName = "command"
}

object CustomLiveSpeechRecognizer : AbstractSpeechRecognizer(configuration) {
    private val logger = Logger.getInstance(javaClass)

    // sphinx4 default sensitivity is 13.
    private const val SPEECH_SENSITIVITY = 20
    private lateinit var microphone: CustomMicrophone

    fun activate() {
        microphone = service()
        microphone.open()

        context.getInstance(StreamDataSource::class.java).setInputStream(microphone.stream)
        context.setLocalProperty("speechClassifier->$PROP_THRESHOLD", SPEECH_SENSITIVITY)
    }

    fun deactivate() {
        microphone.close()
        context.getInstance(StreamDataSource::class.java).setInputStream(InputStream.nullInputStream())
    }

    /**
     * Starts recognition process.
     * @see CustomLiveSpeechRecognizer.stopRecognition
     */
    fun startRecognition() {
//        logger.debug("Recording to file....")
//        CustomMicrophone.recordFromMic(10000)
//        logger.debug("File is ready now")
//
        recognizer.allocate()
        microphone.startRecording()
    }

    /**
     * Stops recognition process.
     * Recognition process is paused until the next call to startRecognition.
     * @see CustomLiveSpeechRecognizer.startRecognition
     */
    fun stopRecognition() {
        microphone.stopRecording()
        recognizer.deallocate()
    }

    fun addResultListener(listener: ResultListener) = recognizer.addResultListener(listener)

    fun removeResultListener(listener: ResultListener) = recognizer.removeResultListener(listener)

    //    public void setMasterGain(double mg) {
    //        microphone.setMasterGain(mg);
    //    }
    //
    //    public void setNoiseLevel(double mg) {
    //        microphone.setNoiseLevel(mg);
    //    }
}
