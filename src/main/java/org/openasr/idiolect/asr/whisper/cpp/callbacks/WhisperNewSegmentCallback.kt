package org.openasr.idiolect.asr.whisper.cpp.callbacks

import com.sun.jna.Callback
import com.sun.jna.Pointer
import org.openasr.idiolect.asr.whisper.cpp.model.WhisperContext
import org.openasr.idiolect.asr.whisper.cpp.model.WhisperState


/**
 * Callback for the text segment.
 * Called on every newly generated text segment.
 * Use the whisper_full_...() functions to obtain the text segments.
 */
interface WhisperNewSegmentCallback : Callback {
    /**
     * Callback method for the text segment.
     *
     * @param ctx        The whisper context.
     * @param state      The whisper state.
     * @param n_new      The number of newly generated text segments.
     * @param user_data  User data.
     */
    fun callback(ctx: WhisperContext?, state: WhisperState?, n_new: Int, user_data: Pointer?)
}

