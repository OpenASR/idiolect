package org.openasr.idiolect.asr.whisper.cpp

import com.sun.jna.Library
import com.sun.jna.Native
import com.sun.jna.Platform
import com.sun.jna.Pointer
import org.openasr.idiolect.asr.whisper.cpp.model.WhisperModelLoader
import org.openasr.idiolect.asr.whisper.cpp.model.WhisperTokenData
import org.openasr.idiolect.asr.whisper.cpp.params.WhisperFullParams


/**
 * This interface is thread-safe as long as the sample whisper_context is not used by multiple threads concurrently.
 *
 * Usage:
 *
 *     Pointer ctx = whisper_init_from_file("/path/to/ggml-base.en.bin");
 *
 *     if (whisper_full(ctx, wparams, pcmf32.data(), pcmf32.size()) != 0) {
 *         log.error("failed to process audio");
 *         return 7;
 *     }
 *
 *     const int nSegments = whisper_full_n_segments(ctx);
 *     for (int i = 0; i < nSegments; ++i) {}
 *         String text = whisper_full_get_segment_text(ctx, i);
 *         log.info(text);
 *     }
 *
 *     whisper_free(ctx);
 */
@Suppress("FunctionName")
interface WhisperJnaLibrary : Library {
    companion object {
        val instance = Native.load(
            if (Platform.isWindows()) "whisper.dll" else "whisper",
            WhisperJnaLibrary::class.java
        )
    }

    /** @return Pointer to the WhisperContext, null if failed to open */
    fun whisper_init_from_file(path_model: String?): Pointer?
    fun whisper_init_from_buffer(buffer: Pointer?, buffer_size: Long): Pointer?
    /** @return Pointer to the WhisperContext, null if the model could not be loaded */
    fun whisper_init(loader: WhisperModelLoader): Pointer?

    fun whisper_init_from_file_no_state(path_model: String?): Pointer?
    fun whisper_init_from_buffer_no_state(buffer: Pointer?, buffer_size: Long): Pointer?
    fun whisper_init_no_state(loader: WhisperModelLoader?): Pointer?

    fun whisper_init_state(ctx: Pointer?): Pointer?

    fun whisper_free(ctx: Pointer?)
    fun whisper_free_state(state: Pointer?)

    fun whisper_print_system_info(): String?

    /**
     * Benchmark function for memcpy.
     *
     * @param nThreads Number of threads to use for the benchmark.
     * @return The result of the benchmark.
     */
    fun whisper_bench_memcpy(nThreads: Int): Int

    /**
     * Benchmark function for memcpy as a string.
     *
     * @param nThreads Number of threads to use for the benchmark.
     * @return The result of the benchmark as a string.
     */
    fun whisper_bench_memcpy_str(nThreads: Int): String?

    /**
     * Benchmark function for ggml_mul_mat.
     *
     * @param nThreads Number of threads to use for the benchmark.
     * @return The result of the benchmark.
     */
    fun whisper_bench_ggml_mul_mat(nThreads: Int): Int

    /**
     * Benchmark function for ggml_mul_mat as a string.
     *
     * @param nThreads Number of threads to use for the benchmark.
     * @return The result of the benchmark as a string.
     */
    fun whisper_bench_ggml_mul_mat_str(nThreads: Int): String?

    fun whisper_full_default_params(strategy: Int): WhisperFullParams

    /**
     * Run the entire model: PCM -> log mel spectrogram -> encoder -> decoder -> text.
     * Not thread safe for same context
     * Uses the specified decoding strategy to obtain the text.
     */
    fun whisper_full(ctx: Pointer, params: WhisperFullParams, samples: FloatArray, nSamples: Int): Int
    fun whisper_full_with_state(ctx: Pointer, state: Pointer, params: WhisperFullParams, samples: FloatArray, nSamples: Int): Int

    /**
     * Split the input audio in chunks and process each chunk separately using whisper_full_with_state()
     * Result is stored in the default state of the context
     * Not thread safe if executed in parallel on the same context.
     * It seems this approach can offer some speedup in some cases.
     * However, the transcription accuracy can be worse at the beginning and end of each chunk.
     */
    fun whisper_full_parallel(ctx: Pointer, params: WhisperFullParams, samples: FloatArray, nSamples: Int, nProcessors: Int): Int

    /**
     * Number of generated text segments
     * A segment can be a few words, a sentence, or even a paragraph.
     */
    fun whisper_full_n_segments(ctx: Pointer): Int
    fun whisper_full_n_segments_from_state(state: Pointer): Int

    /** Language id associated with the context's default state */
    fun whisper_full_lang_id(ctx: Pointer): Int

    /** Language id associated with the provided state */
    fun whisper_full_lang_id_from_state(state: Pointer): Int

    /** Get the start time of the specified segment.  */
    fun whisper_full_get_segment_t0(ctx: Pointer?, i_segment: Int): Long

    /** Get the start time of the specified segment from the state.  */
    fun whisper_full_get_segment_t0_from_state(state: Pointer?, i_segment: Int): Long

    /** Get the end time of the specified segment.  */
    fun whisper_full_get_segment_t1(ctx: Pointer?, i_segment: Int): Long

    /** Get the end time of the specified segment from the state.  */
    fun whisper_full_get_segment_t1_from_state(state: Pointer?, i_segment: Int): Long

    /** Get the text of the specified segment.  */
    fun whisper_full_get_segment_text(ctx: Pointer?, i_segment: Int): String?

    /** Get the text of the specified segment from the state.  */
    fun whisper_full_get_segment_text_from_state(state: Pointer?, i_segment: Int): String?

    /** Get the number of tokens in the specified segment.  */
    fun whisper_full_n_tokens(ctx: Pointer?, i_segment: Int): Int

    /** Get the number of tokens in the specified segment from the state.  */
    fun whisper_full_n_tokens_from_state(state: Pointer?, i_segment: Int): Int

    /** Get the token text of the specified token in the specified segment.  */
    fun whisper_full_get_token_text(ctx: Pointer?, i_segment: Int, i_token: Int): String?

    /** Get the token text of the specified token in the specified segment from the state.  */
    fun whisper_full_get_token_text_from_state(ctx: Pointer?, state: Pointer?, i_segment: Int, i_token: Int): String?

    /** Get the token ID of the specified token in the specified segment.  */
    fun whisper_full_get_token_id(ctx: Pointer?, i_segment: Int, i_token: Int): Int

    /** Get the token ID of the specified token in the specified segment from the state.  */
    fun whisper_full_get_token_id_from_state(state: Pointer?, i_segment: Int, i_token: Int): Int

    /** Get token data for the specified token in the specified segment.  */
    fun whisper_full_get_token_data(ctx: Pointer?, i_segment: Int, i_token: Int): WhisperTokenData?

    /** Get token data for the specified token in the specified segment from the state.  */
    fun whisper_full_get_token_data_from_state(state: Pointer?, i_segment: Int, i_token: Int): WhisperTokenData?

    /** Get the probability of the specified token in the specified segment.  */
    fun whisper_full_get_token_p(ctx: Pointer?, i_segment: Int, i_token: Int): Float

    /** Get the probability of the specified token in the specified segment from the state.  */
    fun whisper_full_get_token_p_from_state(state: Pointer?, i_segment: Int, i_token: Int): Float
}

