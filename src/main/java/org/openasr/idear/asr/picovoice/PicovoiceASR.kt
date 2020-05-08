package org.openasr.idear.asr.picovoice

import com.intellij.openapi.diagnostic.Logger
import org.openasr.idear.asr.ASRProvider
import org.openasr.idear.asr.picovoice.cheetah.CheetahAudioConsumer
import org.openasr.idear.asr.picovoice.cheetah.CheetahCallback
import org.openasr.idear.asr.picovoice.rhino.AudioRecorder
import java.util.concurrent.ArrayBlockingQueue

class PicovoiceASR : ASRProvider, CheetahCallback {
    private val logger = Logger.getInstance(javaClass)

    // TODO: the capacity of the queue could probably be 1...
    private var utterances: ArrayBlockingQueue<String> = ArrayBlockingQueue(10)
    private var recorder: AudioRecorder? = null
    private var audioConsumer: AudioConsumer? = null

    // TODO: provide model & context file paths
//    private var recognizer = PicovoiceRecognizer("TODO", "TODO")

    constructor() {
        audioConsumer = CheetahAudioConsumer(this)
        recorder = AudioRecorder(audioConsumer)
    }

    override fun startRecognition() {
        recorder?.start()
    }

    override fun stopRecognition() {
        recorder?.stop()
    }

    override fun waitForUtterance() = utterances.take()

    override fun onUtterance(utterance: String) {
        logger.info("utterance: $utterance")
        utterances.offer(utterance)
    }
}