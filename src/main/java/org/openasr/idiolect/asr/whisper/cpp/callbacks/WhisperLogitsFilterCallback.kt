package org.openasr.idiolect.asr.whisper.cpp.callbacks

import com.sun.jna.Callback
import com.sun.jna.Pointer
import org.openasr.idiolect.asr.whisper.cpp.model.WhisperContext
import org.openasr.idiolect.asr.whisper.cpp.model.WhisperState
import org.openasr.idiolect.asr.whisper.cpp.model.WhisperTokenData


/**
 * Callback to filter logits.
 * Can be used to modify the logits before sampling.
 * If not null, called after applying temperature to logits.
 */
interface WhisperLogitsFilterCallback : Callback {
    /**
     * Callback method to filter logits.
     *
     * @param ctx        The whisper context.
     * @param state      The whisper state.
     * @param tokens     The array of whisper_token_data.
     * @param n_tokens   The number of tokens.
     * @param logits     The array of logits.
     * @param user_data  User data.
     */
    fun callback(
        ctx: WhisperContext?,
        state: WhisperState?,
        tokens: Array<WhisperTokenData?>?,
        n_tokens: Int,
        logits: FloatArray?,
        user_data: Pointer?
    )
}
