package org.openasr.idiolect.asr.whisper.cpp.params

/** Available sampling strategies */
enum class WhisperSamplingStrategy(val value: Int) {
    /** similar to OpenAI's GreedyDecoder */
    WHISPER_SAMPLING_GREEDY(0),

    /** similar to OpenAI's BeamSearchDecoder */
    WHISPER_SAMPLING_BEAM_SEARCH(1)
}
