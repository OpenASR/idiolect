package org.openasr.idear.asr.picovoice.rhino

import org.openasr.idear.asr.picovoice.AudioConsumer

/**
 * An implementation of {@link ai.picovoice.rhinomanager.AudioConsumer} for Picovoice's speech-to-intent
 * engine (aka Rhino).
 */
class RhinoAudioConsumer : AudioConsumer {
    private var rhino: Rhino? = null
    private var callback: RhinoCallback? = null
    private var isFinalized = false

    /**
     * @param modelFilePath   Absolute path to model file.
     * @param contextFilePath Absolute path to context file.
     * @param rhinoCallback   Callback to be executed upon inference of the intent.
     * @throws RhinoException On failure.
     */
    @Throws(RhinoException::class)
    constructor(modelFilePath: String, contextFilePath: String, rhinoCallback: RhinoCallback?) {
        rhino = Rhino(modelFilePath, contextFilePath)
        this.callback = rhinoCallback
    }

    /**
     * Releases resources acquired by Rhino.
     *
     * @throws RhinoException On failure.
     */
    @Throws(RhinoException::class)
    fun delete() {
        rhino!!.delete()
    }

    @Throws(Exception::class)
    override fun consume(pcm: ShortArray?) {
        if (isFinalized) {
            return
        }
        isFinalized = rhino!!.process(pcm!!)
        if (isFinalized) {
            val isUnderstood = rhino!!.isUnderstood()
            val intent = if (isUnderstood) rhino!!.getIntent() else null
            callback!!.onIntentResult(isUnderstood, intent)
        }
    }

    override fun getFrameLength(): Int {
        return rhino!!.frameLength()
    }

    /** @return sample rate in Hz - eg 16000Hz */
    override fun getSampleRate(): Int {
        return rhino!!.sampleRate()
    }

    /**
     * Resets the internal state of the engine. It should be called before the engine can be used
     * to infer intent from a new stream of audio.
     *
     * @throws RhinoException On failure.
     */
    @Throws(RhinoException::class)
    fun reset() {
        rhino!!.reset()
        isFinalized = false
    }
}