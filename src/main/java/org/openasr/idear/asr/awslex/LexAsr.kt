package org.openasr.idear.asr.awslex

import org.openasr.idear.asr.AsrProvider
import org.openasr.idear.nlp.NlpProvider
import org.openasr.idear.nlp.lex.LexNlp
import org.openasr.idear.recognizer.awslex.LexRecognizer
import java.util.concurrent.ArrayBlockingQueue
import javax.sound.sampled.AudioInputStream


/** Extends LexRecognizer, but implements blocking #waitForUtterance() */
class LexAsr(botName: String = "idear", botAlias: String = "PROD") : LexRecognizer(botName, botAlias), AsrProvider {
    // TODO: the capacity of the queue could probably be 1...
    private var utterances: ArrayBlockingQueue<String> = ArrayBlockingQueue(10)

    override fun displayName() = "Amazon Lex"

    override fun supportsAsrAndNlp(asrProvider: AsrProvider, nlpProvider: NlpProvider): Boolean {
        return asrProvider is LexAsr && nlpProvider is LexNlp
    }

    override fun initialise(asrProvider: AsrProvider, nlpProvider: NlpProvider) {
        assert(asrProvider == this)
    }

    override fun waitForUtterance() = utterances.take()

    override fun setGrammar(grammar: Array<String>) {}

    override fun onVoiceActivity(audioInputStream: AudioInputStream) {
        val result = lex.getRecognizedDataForStream(audioInputStream).result
        utterances.offer(result.inputTranscript)
    }
}
