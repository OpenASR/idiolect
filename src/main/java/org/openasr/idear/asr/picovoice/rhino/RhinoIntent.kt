package org.openasr.idear.asr.picovoice.rhino

/**
 * Data object representing an inferred intent from spoken command.
 */
class RhinoIntent
/**
 * Constructor.
 * @param intent Intent
 * @param slots Intent slots (arguments)
 */ internal constructor(
        val intent: String,
        /** Getter for intent slots (arguments) */
        val slots: Map<String, String>)
