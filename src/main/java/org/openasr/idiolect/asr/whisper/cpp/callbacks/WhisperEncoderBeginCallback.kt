package org.openasr.idiolect.asr.whisper.cpp.callbacks

import com.sun.jna.Callback
import com.sun.jna.Pointer
import org.openasr.idiolect.asr.whisper.cpp.model.WhisperContext
import org.openasr.idiolect.asr.whisper.cpp.model.WhisperState

/**
 * Callback before the encoder starts.
 * If not null, called before the encoder starts.
 * If it returns false, the computation is aborted.
 */
interface WhisperEncoderBeginCallback : Callback {
    /**
     * Callback method before the encoder starts.
     *
     * @param ctx        The whisper context.
     * @param state      The whisper state.
     * @param userData  User data.
     * @return True if the computation should proceed, false otherwise.
     */
    fun callback(ctx: WhisperContext?, state: WhisperState?, userData: Pointer?): Boolean
}

