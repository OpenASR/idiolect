package org.openasr.idiolect.asr.whisper.cpp.callbacks

import com.sun.jna.Pointer
import org.openasr.idiolect.asr.whisper.cpp.model.WhisperContext
import org.openasr.idiolect.asr.whisper.cpp.model.WhisperState
import javax.security.auth.callback.Callback;

/**
 * Callback for progress updates.
 */
interface WhisperProgressCallback : Callback {
    /**
     * Callback method for progress updates.
     *
     * @param ctx        The whisper context.
     * @param state      The whisper state.
     * @param progress   The progress value.
     * @param user_data  User data.
     */
    fun callback(ctx: WhisperContext?, state: WhisperState?, progress: Int, user_data: Pointer?)
}
