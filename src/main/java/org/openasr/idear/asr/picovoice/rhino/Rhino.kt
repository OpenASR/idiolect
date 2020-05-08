package org.openasr.idear.asr.picovoice.rhino

import org.mozilla.javascript.RhinoException


/**
 * Binding for Picovoice's Speech-to-Intent engine (Rhino). The engine directly infers intent
 * from spoken commands within a given domain of interest in real-time. It processes incoming audio
 * in consecutive frames (chunks) and at the end of each frame indicates if the intent inference is
 * finalized. When finalized, the intent can be retrieved as structured data in form of an intent
 * string and pairs of slots and values representing arguments (details) of intent. The number of
 * samples per frame can be attained by calling {@link #frameLength()}. The incoming audio needs to
 * have a sample rate equal to {@link #sampleRate()} and be 16-bit linearly-encoded. Furthermore,
 * Rhino operates on single channel audio.
 *
 * Adapted from https://github.com/Picovoice/rhino
 */
class Rhino {
    init {
        System.loadLibrary("pv_rhino")
    }

    private var rhino: Long = 0

    /**
     * @param modelFilePath   Absolute path to file containing model parameters.
     * @param contextFilePath Absolute path to file containing context parameters. A context
     * represents the set of expressions (commands), intents, and intent
     * arguments (slots) within a domain of interest.
     * @throws RhinoException On failure.
     */
    @Throws(RhinoException::class)
    constructor(modelFilePath: String, contextFilePath: String) {
        rhino = try {
            init(modelFilePath, contextFilePath)
        } catch (e: Exception) {
            throw RhinoException(e)
        }
    }

    /**
     * Destructor. This needs to be called explicitly as we do not rely on garbage collector.
     *
     * @throws RhinoException On failure.
     */
    @Throws(RhinoException::class)
    fun delete() {
        try {
            delete(rhino)
        } catch (e: Exception) {
            throw RhinoException(e)
        }
    }

    /**
     * Processes a frame of audio and emits a flag indicating if the engine has finalized intent
     * extraction. When finalized, [.isUnderstood] should be called to check if the command
     * was valid (is within context of interest) and is understood.
     *
     * @param pcm A frame of audio samples. The number of samples per frame can be attained by
     * calling [.frameLength]. The incoming audio needs to have a sample rate
     * equal to [.sampleRate] and be 16-bit linearly-encoded. Furthermore,
     * Rhino operates on single channel audio.
     * @return Flag indicating whether the engine has finalized intent extraction.
     * @throws RhinoException On failure.
     */
    @Throws(RhinoException::class)
    fun process(pcm: ShortArray): Boolean {
        return try {
            process(rhino, pcm)
        } catch (e: Exception) {
            throw RhinoException(e)
        }
    }

    /**
     * Indicates if the spoken command is valid, is within the domain of interest (context), and the
     * engine understood it.
     *
     * @return Flag indicating if the spoken command is valid, is within the domain of interest
     * (context), and the engine understood it.
     * @throws RhinoException On failure.
     */
    @Throws(RhinoException::class)
    fun isUnderstood(): Boolean {
        return try {
            isUnderstood(rhino)
        } catch (e: Exception) {
            throw RhinoException(e)
        }
    }

    /**
     * Getter for the intent inferred from spoken command. The intent is presented as an intent
     * string and pairs of slots and their values. It should be called only after intent extraction
     * is finalized and it is verified that the spoken command is valid and understood via calling
     * [.isUnderstood].
     *
     * @return Inferred intent object.
     * @throws RhinoException On failure.
     */
    @Throws(RhinoException::class)
    fun getIntent(): RhinoIntent? {
        val intentPacked = getIntent(rhino)
        val parts = intentPacked.split(",".toRegex()).toTypedArray()
        if (parts.size == 0) {
            throw RhinoException(String.format("failed to retrieve intent from %s", intentPacked))
        }
        val slots: MutableMap<String, String> = LinkedHashMap()
        for (i in 1 until parts.size) {
            val slotAndValue = parts[i].split(":".toRegex()).toTypedArray()
            if (slotAndValue.size != 2) {
                throw RhinoException(String.format("failed to retrieve intent from %s", intentPacked))
            }
            slots[slotAndValue[0]] = slotAndValue[1]
        }
        return RhinoIntent(parts[0], slots)
    }

    /**
     * Resets the internal state of the engine. It should be called before the engine can be used to
     * infer intent from a new stream of audio.
     *
     * @throws RhinoException On failure.
     */
    @Throws(RhinoException::class)
    fun reset() {
        try {
            reset(rhino)
        } catch (e: Exception) {
            throw RhinoException(e)
        }
    }

    /**
     * Getter for expressions. Each expression maps a set of spoken phrases to an intent and
     * possibly a number of slots (intent arguments).
     *
     * @return Expressions.
     * @throws RhinoException On failure.
     */
    @Throws(RhinoException::class)
    fun getContextInformation(): String? {
        return try {
            contextExpressions(rhino)
        } catch (e: Exception) {
            throw RhinoException(e)
        }
    }

    /**
     * Getter for length (number of audio samples) per frame.
     *
     * @return Frame length.
     */
    external fun frameLength(): Int

    /**
     * Audio sample rate accepted by Picovoice.
     *
     * @return Sample rate.
     */
    external fun sampleRate(): Int

    /**
     * Getter for version string.
     *
     * @return Version string.
     */
    external fun version(): String?

    private external fun init(model_file_path: String, context_file_path: String): Long

    private external fun delete(`object`: Long)

    private external fun process(`object`: Long, pcm: ShortArray): Boolean

    private external fun isUnderstood(`object`: Long): Boolean

    private external fun getIntent(`object`: Long): String

    private external fun reset(`object`: Long): Boolean

    private external fun contextExpressions(`object`: Long): String?
}