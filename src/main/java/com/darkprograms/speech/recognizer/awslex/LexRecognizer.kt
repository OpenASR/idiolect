package com.darkprograms.speech.recognizer.awslex

import com.amazonaws.services.lexruntime.AmazonLexRuntime
import com.amazonaws.services.lexruntime.model.PostContentRequest
import javax.sound.sampled.AudioInputStream

/**
 * example:
 * <pre>
 * LexRecognizer lex = new LexRecognizer(AmazonLexRuntimeClientBuilder.defaultClient(), "MyLexBot", "PROD", "auser");
 * MicrophoneAnalyzer mic = new MicrophoneAnalyzer(null);
 * VoiceActivityDetector vad = new VoiceActivityDetector();
 *
 * vad.detectVoiceActivity(mic, audioInputStream -> {
 * PostContentResult result = lex.getRecognizedDataForStream(audioInputStream, myApp.getSessionAttributes()).getResult();
 * System.out.println(result.message);
 * });
</pre> *
 */
class LexRecognizer(private val lex: AmazonLexRuntime, private val botName: String, private val botAlias: String, private var userId: String?) {

    fun setUserId(userId: String) {
        this.userId = userId
    }

    /**
     * @param stream
     * @param sessionAttributes The value must be map (keys and values must be strings) that is JSON serialized and then base64 encoded
     * @return
     */
    @JvmOverloads
    fun getRecognizedDataForStream(stream: AudioInputStream, sessionAttributes: String? = null): LexResponse {
        val request = PostContentRequest()
                .withBotName(botName)
                .withBotAlias(botAlias)
                .withUserId(userId)
                .withInputStream(stream)
                .withContentType("audio/l16; rate=16000; channels=1")
                .withSessionAttributes(sessionAttributes)

        //      System.out.println("sending request to Lex: " + request);
        //        try {System.out.println(">> " + stream.available());} catch (IOException e) {}

        val result = lex.postContent(request)
        return LexResponse(result)
    }
}
