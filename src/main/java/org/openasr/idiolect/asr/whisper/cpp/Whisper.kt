package org.openasr.idiolect.asr.whisper.cpp

import com.intellij.openapi.diagnostic.logger
import com.sun.jna.Pointer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.withContext
import org.openasr.idiolect.asr.whisper.cpp.params.WhisperFullParams
import org.openasr.idiolect.asr.whisper.cpp.params.WhisperSamplingStrategy
import java.io.FileNotFoundException
import java.io.IOException
import java.util.concurrent.Executors

/** Adapted from the whisper.cpp Android example */
object Whisper : AutoCloseable {
    private val lib = WhisperJnaLibrary.instance
    private var ctx: Pointer? = null

    // Meet Whisper C++ constraint: Don't access from more than one thread at a time.
    private val scope: CoroutineScope = CoroutineScope(
        Executors.newSingleThreadExecutor().asCoroutineDispatcher()
    )

//    companion object {
    private val log = logger<Whisper>()

//        init {
//            var loadVfpv4 = false
//            var loadV8fp16 = false
//            if (isArmEabiV7a()) {
//                // armeabi-v7a needs runtime detection support
//                val cpuInfo = cpuInfo()
//                cpuInfo?.let {
//                    Log.d(LOG_TAG, "CPU info: $cpuInfo")
//                    if (cpuInfo.contains("vfpv4")) {
//                        Log.d(LOG_TAG, "CPU supports vfpv4")
//                        loadVfpv4 = true
//                    }
//                }
//            } else if (isArmEabiV8a()) {
//                // ARMv8.2a needs runtime detection support
//                val cpuInfo = cpuInfo()
//                cpuInfo?.let {
//                    Log.d(LOG_TAG, "CPU info: $cpuInfo")
//                    if (cpuInfo.contains("fphp")) {
//                        Log.d(LOG_TAG, "CPU supports fp16 arithmetic")
//                        loadV8fp16 = true
//                    }
//                }
//            }
//
//            if (loadVfpv4) {
//                Log.d(LOG_TAG, "Loading libwhisper_vfpv4.so")
//                System.loadLibrary("whisper_vfpv4")
//            } else if (loadV8fp16) {
//                Log.d(LOG_TAG, "Loading libwhisper_v8fp16_va.so")
//                System.loadLibrary("whisper_v8fp16_va")
//            } else {
//                Log.d(LOG_TAG, "Loading libwhisper.so")
//                System.loadLibrary("whisper")
//            }
//        }


//    fun initContextFromInputStream(inputStream: InputStream): Pointer {
//        log.warn("initContextFromInputStream probably won't work - it's incomplete & untested")
////            val context = with(WhisperContext(TODO)) {
////                offset = 0
////                inputStream = inputStream
////                mid_available = inputStream.available()
////                mid_read = inputStream.read()
////            }
//
//        val loader = WhisperModelLoader(inputStream)
//        return lib.whisper_init(loader) ?: throw InvalidObjectException("Invalid model")
//    }
//        external fun initContextFromAsset(assetManager: AssetManager, assetPath: String): Long

    /**
     * @param modelPath
     * @return a Pointer to the WhisperContext
     */
    fun initContext(modelPath: String) {
        if (ctx != null) {
            lib.whisper_free(ctx)
        }
        log.info("Initialising WhisperContext from '$modelPath'...")
        ctx = lib.whisper_init_from_file(modelPath) ?: throw FileNotFoundException(modelPath)
    }

    fun getDefaultParams(strategy: WhisperSamplingStrategy): WhisperFullParams {
        return lib.whisper_full_default_params(strategy.value)
    }

    override fun close() {
        freeContext()
        log.debug("Whisper closed")
    }

    private fun freeContext() {
        if (ctx != null) {
            lib.whisper_free(ctx)
        }
    }

    /**
     * Run the entire model: PCM -> log mel spectrogram -> encoder -> decoder -> text.
     * Not thread safe for same context
     * Uses the specified decoding strategy to obtain the text.
     */
    suspend fun fullTranscribe(whisperParams: WhisperFullParams, audioData: FloatArray): String = withContext(scope.coroutineContext) {
        if (ctx == null) {
            throw IllegalStateException("Model not initialised")
        }

        if (lib.whisper_full(ctx!!, whisperParams, audioData, audioData.size) != 0) {
            throw IOException("Failed to process audio")
        }

        val nSegments = lib.whisper_full_n_segments(ctx!!)

        return@withContext buildString {
            for (i in 0 until nSegments) {
                val text = lib.whisper_full_get_segment_text(ctx, i);
                log.debug("Segment: $text")
                append(text)
            }
        }
    }

//    fun getTextSegmentCount(ctx: Pointer): Int = lib.whisper_full_n_segments(ctx)
//    fun getTextSegment(ctx: Pointer, index: Int) = lib.whisper_full_get_segment_text(ctx, index)

    fun getSystemInfo() = lib.whisper_print_system_info()
    fun benchMemcpy(nthread: Int) = lib.whisper_bench_memcpy(nthread)
    fun benchGgmlMulMat(nthread: Int) = lib.whisper_bench_ggml_mul_mat(nthread)
}
