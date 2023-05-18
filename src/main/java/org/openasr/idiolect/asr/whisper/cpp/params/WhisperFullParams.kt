package org.openasr.idiolect.asr.whisper.cpp.params

import com.sun.jna.Pointer
import com.sun.jna.Structure


/**
 * Parameters for the whisper_full() function.
 * If you change the order or add new parameters, make sure to update the default values in whisper.cpp:
 * whisper_full_default_params()
 */
class WhisperFullParams : Structure() {
    /** Sampling strategy for whisper_full() function.  */
    var strategy = 0

    /** Number of threads.  */
    var n_threads = 0

    /** Maximum tokens to use from past text as a prompt for the decoder.  */
    var n_max_text_ctx = 0

    /** Start offset in milliseconds.  */
    var offset_ms = 0

    /** Audio duration to process in milliseconds.  */
    var duration_ms = 0

    /** Translate flag.  */
    var translate = false

    /** Flag to indicate whether to use past transcription (if any) as an initial prompt for the decoder.  */
    var no_context = false

    /** Flag to force single segment output (useful for streaming).  */
    var single_segment = false

    /** Flag to print special tokens (e.g., <SOT>, <EOT>, <BEG>, etc.). </BEG></EOT></SOT> */
    var print_special = false

    /** Flag to print progress information.  */
    var print_progress = false

    /** Flag to print results from within whisper.cpp (avoid it, use callback instead).  */
    var print_realtime = false

    /** Flag to print timestamps for each text segment when printing realtime.  */
    var print_timestamps = false

    /** [EXPERIMENTAL] Flag to enable token-level timestamps.  */
    var token_timestamps = false

    /** [EXPERIMENTAL] Timestamp token probability threshold (~0.01).  */
    var thold_pt = 0f

    /** [EXPERIMENTAL] Timestamp token sum probability threshold (~0.01).  */
    var thold_ptsum = 0f

    /** Maximum segment length in characters.  */
    var max_len = 0

    /** Flag to split on word rather than on token (when used with max_len).  */
    var split_on_word = false

    /** Maximum tokens per segment (0 = no limit).  */
    var max_tokens = 0

    /** Flag to speed up the audio by 2x using Phase Vocoder.  */
    var speed_up = false

    /** Overwrite the audio context size (0 = use default).  */
    var audio_ctx = 0

    /** Tokens to provide to the whisper decoder as an initial prompt.
     * These are prepended to any existing text context from a previous call.  */
    var initial_prompt: String? = null

    /** Prompt tokens.  */
    var prompt_tokens: Pointer? = null

    /** Number of prompt tokens.  */
    var prompt_n_tokens = 0

    /** Language for auto-detection.
     * For auto-detection, set to `null`, `""`, or "auto".  */
    var language: String? = null

    /** Flag to indicate whether to detect language automatically.  */
    var detect_language = false
    /** Common decoding parameters.  */
    /** Flag to suppress blank tokens.  */
    var suppress_blank = false

    /** Flag to suppress non-speech tokens.  */
    var suppress_non_speech_tokens = false

    /** Initial decoding temperature.  */
    var temperature = 0f

    /** Maximum initial timestamp.  */
    var max_initial_ts = 0f

    /** Length penalty.  */
    var length_penalty = 0f
    /** Fallback parameters.  */
    /** Temperature increment.  */
    var temperature_inc = 0f

    /** Entropy threshold (similar to OpenAI's "compression_ratio_threshold").  */
    var entropy_thold = 0f

    /** Log probability threshold.  */
    var logprob_thold = 0f

    /** No speech threshold.  */
    var no_speech_thold = 0f

    inner class GreedyParams : Structure() {
        /** https://github.com/openai/whisper/blob/f82bc59f5ea234d4b97fb2860842ed38519f7e65/whisper/transcribe.py#L264  */
        var best_of = 0
    }

    /** Greedy decoding parameters.  */
    var greedy: GreedyParams? = null

    inner class BeamSearchParams : Structure() {
        /** ref: https://github.com/openai/whisper/blob/f82bc59f5ea234d4b97fb2860842ed38519f7e65/whisper/transcribe.py#L265  */
        var beam_size = 0

        /** ref: https://arxiv.org/pdf/2204.05424.pdf  */
        var patience = 0f
    }

    /** Beam search decoding parameters. */
    var beam_search: BeamSearchParams? = null

//    /** Callback for every newly generated text segment. */
//    var new_segment_callback: WhisperNewSegmentCallback? = null

    /** User data for the new_segment_callback. */
    var new_segment_callback_user_data: Pointer? = null

//    /** Callback on each progress update. */
//    var progress_callback: WhisperProgressCallback? = null

    /** User data for the progress_callback. */
    var progress_callback_user_data: Pointer? = null

//    /** Callback each time before the encoder starts. */
//    var encoder_begin_callback: WhisperEncoderBeginCallback? = null

    /** User data for the encoder_begin_callback. */
    var encoder_begin_callback_user_data: Pointer? = null

//    /** Callback by each decoder to filter obtained logits. */
//    var logits_filter_callback: WhisperLogitsFilterCallback? = null

    /** User data for the logits_filter_callback. */
    var logits_filter_callback_user_data: Pointer? = null
}
