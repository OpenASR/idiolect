package org.openasr.idear.asr.awslex

import org.openasr.idear.asr.ASRProvider
import org.openasr.idear.recognizer.awslex.LexRecognizer
import java.util.concurrent.ArrayBlockingQueue
import javax.sound.sampled.AudioInputStream


/** Extends LexRecognizer, but implements blocking #waitForUtterance() */
class LexASR(botName: String = "idear", botAlias: String = "PROD") : LexRecognizer(botName, botAlias), ASRProvider {
    // TODO: the capacity of the queue could probably be 1...
    private var utterances: ArrayBlockingQueue<String> = ArrayBlockingQueue(10)

    override fun waitForUtterance(): String = utterances.take()

    override fun onVoiceActivity(audioInputStream: AudioInputStream) {
        val result = lex.getRecognizedDataForStream(audioInputStream).result
        utterances.offer(result.inputTranscript)
    }
}
