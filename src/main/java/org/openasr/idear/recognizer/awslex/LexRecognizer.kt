package org.openasr.idear.recognizer.awslex

import com.amazonaws.services.lexruntime.AmazonLexRuntimeClientBuilder
import com.amazonaws.services.lexruntime.model.DialogState
import com.darkprograms.speech.microphone.MicrophoneAnalyzer
import com.darkprograms.speech.recognizer.vad.SimpleVAD
import com.darkprograms.speech.recognizer.vad.VoiceActivityDetector
import com.darkprograms.speech.recognizer.vad.VoiceActivityListener
import org.json.JSONObject
import org.openasr.idear.asr.ASRSystem
import org.openasr.idear.asr.awslex.LexASR
import org.openasr.idear.nlp.LoggingNlpResultListener
import org.openasr.idear.nlp.NlpResultListener
import org.openasr.idear.recognizer.SpeechRecognizer
import java.util.logging.Level
import java.util.logging.Logger
import javax.sound.sampled.AudioInputStream
import com.darkprograms.speech.recognizer.awslex.LexRecognizer as JarvisLex

/**
 * [AWS Lex](http://docs.aws.amazon.com/lex/latest/dg/what-is.html) can process speech and return recognised text,
 * but can also do further NLP processing and convert the text into actions with parameters.
 *
 * @see LexASR which implements #waitForUtterance() instead of posting to NlpResultListener
 */
// TODO: should LexRecognizer and LexASR be swapped around?
open class LexRecognizer(val botName: String = "Idear", val botAlias: String = "PROD") : SpeechRecognizer, VoiceActivityListener, ASRSystem {
    private val mic = MicrophoneAnalyzer(16_000F)
    private val vad: VoiceActivityDetector = SimpleVAD()
    protected val lex: JarvisLex
    private var nlpListener: NlpResultListener = LoggingNlpResultListener()

    init {
        var lexRuntime = AmazonLexRuntimeClientBuilder
                            .standard()
                            .withRegion(AwsUtils.REGION)
                            .withCredentials(AwsUtils.credentialsProvider)
                            .build()
        lex = JarvisLex(lexRuntime, botName, botAlias, "anonymous")
    }

    fun setUserId(userId: String) = lex.setUserId(userId)

    fun setNlpResultListener(listener: NlpResultListener) {
        nlpListener = listener
    }

    override fun start() = startRecognition()

    override fun waitForUtterance(): String {
        // Temporarily swap listeners
        val asr = LexASR(botName, botAlias)
        vad.setVoiceActivityListener(asr)
        val utterance = asr.waitForUtterance()
        vad.setVoiceActivityListener(this)
        return utterance
    }


    /** This starts a new JARVIS VAD thread which calls onVoiceActivity() with results */
    override fun startRecognition() = vad.detectVoiceActivity(mic, this)
//        // debug versions
//        // just record utterances to /tmp
//        vad.detectVoiceActivity(mic, RecordingListener())
//        // record to /tmp, but also call onVoiceActivity() below
//        vad.detectVoiceActivity(mic, RecordingListener().withNextListener(this))
    override fun stopRecognition() = mic.close()

    override fun terminate() {
        mic.close()
        vad.terminate()
    }

    override fun onVoiceActivity(audioInputStream: AudioInputStream) {
        logger.log(Level.FINE, "processing speech...", audioInputStream.frameLength)

        val result = lex.getRecognizedDataForStream(audioInputStream).result
        logger.log(Level.FINE, "Recognition result", result)
//        println("Lex recognized: " + result.inputTranscript)

        when (result.dialogState) {
            DialogState.Fulfilled.name -> {
                val slots: MutableMap<String, String>? = if (result.slots == null) {
                    null
                } else {
                    JSONObject(result.slots).toMap() as MutableMap<String, String>
//                val json = JSONObject(result.slots)
//                val map = HashMap<String, String>(json.length())
//                for (key in json.keys()) {
//                    map[key] = json[key] as String
//                }
//                map
                }

                // TODO: Intent "Navigate" Lex sends back a response with sessionAttribute:
                //    {"invokeAction": "GotoDeclaration"}
                // ...but it won't let us send back an empty `message` in Content
                // - need to ignore or add an extra sesssionAttribute indicating verbosity

                val sessionAttributes = if (result.sessionAttributes == null) {
                    null
                } else {
                    JSONObject(result.sessionAttributes).toMap() as MutableMap<String, String>
                }

//                result.message

                nlpListener.onFulfilled(result.intentName, slots, sessionAttributes)
                nlpListener.onMessage(result.message, NlpResultListener.Companion.Verbosity.valueOf(sessionAttributes?.get("Verbosity") ?: "ALL"))
            }
            else -> {
                nlpListener.onFailure(result.message)
            }
        }
    }

    private fun stringToMap(json: String?) = if (json == null) { null } else { JSONObject(json).toMap() }

    companion object {
        private val logger = Logger.getLogger(LexRecognizer::class.java.simpleName)
    }
}
