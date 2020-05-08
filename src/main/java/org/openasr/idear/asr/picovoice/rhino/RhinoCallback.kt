package org.openasr.idear.asr.picovoice.rhino

/**
 * Callback to be called by Rhino (Picovoice's speech to intent engine) upon inferring user's intent.
 */
interface RhinoCallback {
    /**
     * Callback function.
     *
     * @param isUnderstood Flag indicating if the spoken command is understood and is within domain
     * of interest.
     * @param intent       User's intent.
     */
    fun onIntentResult(isUnderstood: Boolean, intent: RhinoIntent?)
}