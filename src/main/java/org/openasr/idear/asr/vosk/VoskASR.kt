package org.openasr.idear.asr.vosk

import org.openasr.idear.asr.ASRProvider
import org.openasr.idear.recognizer.CustomMicrophone
import org.vosk.LibVosk
import org.vosk.Model
import org.vosk.Recognizer


class VoskASR(modelPath: String) : ASRProvider {
    private val model = Model(modelPath)
    private val recognizer = Recognizer(model, 16000f)

//    static {
//        System.loadLibrary("vosk_jni")
//    }

    init {
        System.loadLibrary("libvosk")
        LibVosk.setLogLevel(org.vosk.LogLevel.DEBUG)
    }

    /**
     * Starts recognition process.
     */
    override fun startRecognition() {
        CustomMicrophone.startRecording()
    }

    /**
     * Stops recognition process.
     * Recognition process is paused until the next call to startRecognition.
     */
    override fun stopRecognition() {
        CustomMicrophone.stopRecording()
    }

    /** Blocks until a we recognise something from the user. Called from [ASRControlLoop.run] */
    override fun waitForUtterance(): String {
        var nbytes: Int
        val b = ByteArray(4096)
        while (CustomMicrophone.stream.read(b).also { nbytes = it } >= 0) {
            if (recognizer.acceptWaveForm(b, nbytes)) {
                println(recognizer.result)
            } else {
                println(recognizer.partialResult)
            }
        }

        return recognizer.finalResult;
    }
}
