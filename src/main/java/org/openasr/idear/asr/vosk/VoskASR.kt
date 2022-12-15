package org.openasr.idear.asr.vosk

import com.intellij.openapi.components.service
import com.sun.jna.Native
import com.sun.jna.Platform
import edu.cmu.sphinx.frontend.endpoint.SpeechClassifier
import edu.cmu.sphinx.frontend.util.StreamDataSource
import org.openasr.idear.asr.AsrProvider
import org.openasr.idear.asr.cmusphinx.CustomLiveSpeechRecognizer
import org.openasr.idear.recognizer.CustomMicrophone
import org.vosk.LibVosk
import org.vosk.Model
import org.vosk.Recognizer
import java.io.File
import java.io.InputStream


class VoskASR : AsrProvider {
    private var recognizer: Recognizer? = null

    var modelPath: String? = defaultModel()
        set(value) {
            recognizer = Recognizer(Model(value), 16000f)
        }

    init {
        if (Platform.isWindows()) {
            // To get a tmp folder we unpack small library and mark it for deletion
            val tmpFile = Native.extractFromResourcePath("/win32-x86-64/empty", Recognizer::class.java.classLoader)
            val tmpDir = tmpFile.parentFile
            File(tmpDir, tmpFile.name + ".x").createNewFile()

//            ApplicationManager.getApplication().
//            PlatformUtils.
        }
//        System.loadLibrary("libvosk")
        LibVosk.setLogLevel(org.vosk.LogLevel.DEBUG)
    }

    override fun displayName() = "Vosk"

    override fun defaultModel() = System.getProperty("user.home") + "/.ideaLibSources/vosk-model-en-us-daanzu-20200905-lgraph"

    private lateinit var microphone: CustomMicrophone

    override fun activate() {
        microphone = service()
        microphone.open()
    }

    override fun deactivate() {
        microphone.close()
    }

    /**
     * Starts recognition process.
     */
    override fun startRecognition() {
        microphone.startRecording()
    }

    /**
     * Stops recognition process.
     * Recognition process is paused until the next call to startRecognition.
     */
    override fun stopRecognition() {
        microphone.stopRecording()
    }

    /** Blocks until we recognise something from the user. Called from [ASRControlLoop.run] */
    override fun waitForUtterance(): String {
        var nbytes: Int
        val b = ByteArray(4096)
        while (microphone.stream.read(b).also { nbytes = it } >= 0) {
            val rec = recognizer;

            if (rec != null) {
                if (rec.acceptWaveForm(b, nbytes)) {
                    println(rec.result)
                } else {
                    println(rec.partialResult)
                }

                return rec.finalResult
            }
        }

        return ""
    }
}
