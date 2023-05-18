package org.openasr.idiolect.asr.whisper.cpp.model

import com.sun.jna.Structure

/**
 * Structure representing token data.
 */
class WhisperTokenData : Structure() {
    /** Token ID.  */
    var id = 0

    /** Forced timestamp token ID.  */
    var tid = 0

    /** Probability of the token.  */
    var p = 0f

    /** Log probability of the token.  */
    var plog = 0f

    /** Probability of the timestamp token.  */
    var pt = 0f

    /** Sum of probabilities of all timestamp tokens.  */
    var ptsum = 0f

    /**
     * Start time of the token (token-level timestamp data).
     * Do not use if you haven't computed token-level timestamps.
     */
    var t0: Long = 0

    /**
     * End time of the token (token-level timestamp data).
     * Do not use if you haven't computed token-level timestamps.
     */
    var t1: Long = 0

    /** Voice length of the token.  */
    var vlen = 0f
    override fun getFieldOrder(): List<String> {
        return mutableListOf("id", "tid", "p", "plog", "pt", "ptsum", "t0", "t1", "vlen")
    }
}
