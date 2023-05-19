package org.openasr.idiolect.asr.whisper.cpp

import com.sun.jna.Library
import com.sun.jna.Native
import com.sun.jna.Pointer
import org.openasr.idiolect.asr.whisper.cpp.params.WhisperFullParams
import org.openasr.idiolect.asr.whisper.cpp.params.WhisperJavaParams

interface WhisperJavaJnaLibrary : Library {
    companion object {
        val instance = Native.load(
            "whisper_java",
//            if (Platform.isWindows()) "whisper.dll" else "whisper",
            WhisperJavaJnaLibrary::class.java
        )
    }

    fun whisper_java_default_params(strategy: Int): WhisperJavaParams?

    fun whisper_java_free()

    /**
     * Run the entire model: PCM -> log mel spectrogram -> encoder -> decoder -> text.
     * Not thread safe for same context
     * Uses the specified decoding strategy to obtain the text.
     */
    fun whisper_java_full(ctx: Pointer, params: WhisperJavaParams, samples: FloatArray, nSamples: Int): Int
//    fun whisper_java_full_with_state(ctx: Pointer, state: Pointer, params: WhisperJavaParams, samples: FloatArray, nSamples: Int): Int

//    /**
//     * Split the input audio in chunks and process each chunk separately using whisper_full_with_state()
//     * Result is stored in the default state of the context
//     * Not thread safe if executed in parallel on the same context.
//     * It seems this approach can offer some speedup in some cases.
//     * However, the transcription accuracy can be worse at the beginning and end of each chunk.
//     */
//    fun whisper_java_full_parallel(ctx: Pointer, params: WhisperJavaParams, samples: FloatArray, nSamples: Int, nProcessors: Int): Int
}
