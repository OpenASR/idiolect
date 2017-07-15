package org.openasr.idear.recognizer.awslex

import com.amazonaws.services.lexruntime.AmazonLexRuntimeClientBuilder
import com.amazonaws.services.lexruntime.model.DialogState
import com.darkprograms.speech.microphone.MicrophoneAnalyzer
import com.darkprograms.speech.recognizer.vad.RecordingListener
import com.darkprograms.speech.recognizer.vad.SimpleVAD
import com.darkprograms.speech.recognizer.vad.VoiceActivityDetector
import com.darkprograms.speech.recognizer.vad.VoiceActivityListener
import org.json.JSONObject
import org.openasr.idear.nlp.LoggingNlpResultListener
import org.openasr.idear.nlp.NlpResultListener
import org.openasr.idear.recognizer.SpeechRecognizer
import java.util.logging.Logger
import javax.sound.sampled.AudioInputStream
import com.darkprograms.speech.recognizer.awslex.LexRecognizer as JarvisLex

class LexRecognizer : SpeechRecognizer, VoiceActivityListener {
    private val mic = MicrophoneAnalyzer(16_000F)
    private val vad: VoiceActivityDetector = SimpleVAD()
    private val lex: JarvisLex
    private var nlpListener: NlpResultListener = LoggingNlpResultListener()

    constructor(botName: String = "idear", botAlias: String = "PROD") {
        var lexRuntime = AmazonLexRuntimeClientBuilder
                            .standard()
                            .withRegion("us-east-1")
                            .build()
        lex = JarvisLex(lexRuntime, "idear", "PROD", "anonymous")
    }

    fun setUserId(userId: String) = lex.setUserId(userId)

    fun setNlpResultListener(listener: NlpResultListener) {
        nlpListener = listener
    }

    override fun startRecognition() {
//        vad.detectVoiceActivity(mic, this)
        vad.detectVoiceActivity(mic, RecordingListener()) //.withNextListener(this))
    }
    override fun stopRecognition() = mic.close()

    override fun onVoiceActivity(audioInputStream: AudioInputStream) {
//        logger.log(Level.FINE, "processing speech....", audioInputStream.frameLength)

        val result = lex.getRecognizedDataForStream(audioInputStream).result
//        logger.log(Level.FINE, "Recognition result", result)
println("Lex recognized: " + result.inputTranscript)

        when (result.dialogState) {
            DialogState.Fulfilled.name -> {
                val slots: Map<String, String>? = if (result.slots == null) {
                    null
                } else {
                    JSONObject(result.slots).toMap() as Map<String, out String>
//                val json = JSONObject(result.slots)
//                val map = HashMap<String, String>(json.length())
//                for (key in json.keys()) {
//                    map[key] = json[key] as String
//                }
//                map
                }

                nlpListener.onFulfilled(result.intentName, slots)
            }
            else -> {
                nlpListener.onFailure(result.message)
            }
        }
    }

    companion object {
        private val logger = Logger.getLogger(LexRecognizer::class.java.simpleName)
    }
}
