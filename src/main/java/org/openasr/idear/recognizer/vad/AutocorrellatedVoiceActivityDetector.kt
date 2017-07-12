package org.openasr.idear.recognizer.vad

import edu.cmu.sphinx.frontend.DataProcessingException
import edu.cmu.sphinx.frontend.util.DataUtil
import org.openasr.idear.recognizer.CustomMicrophone
import java.io.DataInputStream
import java.io.InputStream
import java.util.logging.Logger
import javax.sound.sampled.AudioInputStream
import javax.sound.sampled.AudioSystem
import org.apache.commons.lang3.Conversion.byteArrayToShort
import java.io.IOException


/**
 * A voice activity detector attempts to detect presence or abscence of voice in the signal.
 * <p>
 * The technique used here is a simple (but efficient) one based on a characteristic of (white) noise :
 * when applying autocorrelation, the mean value of the computed cofficients gets close to zero. <br/>
 * Voice activity detection has undergone quite a lot of research, best algorithms use several hints before deciding presence or
 * absence of voice.
 * </p>
 * @see <a href="http://en.wikipedia.org/wiki/White_noise">White noise</a>
 * @see <a href="http://en.wikipedia.org/wiki/Autocorrelation">Autocorrelation</a>
 * @see <a href="http://en.wikipedia.org/wiki/Voice_activity_detection">Voice activity detection</a>
 * @see <a href="http://ieeexplore.ieee.org/xpl/articleDetails.jsp?arnumber=6403507&punumber%3D97">Unsupervised VAD article on IEEE</a>
 *
 * TODO: incorporate https://www.researchgate.net/publication/255667085_A_simple_but_efficient_real-time_voice_activity_detection_algorithm
 * @author Amaury Crickx
 */
class AutocorrellatedVoiceActivityDetector { //(val sampleRate: Int = 16_000) {
    private val WINDOW_MILLIS = 1
    private val FADE_MILLIS = 2
    private val MIN_SILENCE_MILLIS = 4
    private val MIN_VOICE_MILLIS = 200
    private val MAX_VOICE_MILLIS = 60_000

    /** the noise threshold used to determine if a given section is silence or not */
    var threshold = 0.0001

    private var fadeInFactors: DoubleArray? = null
    private var fadeOutFactors: DoubleArray? = null

    fun monitorVoiceActivity(inStream: AudioInputStream, listener: VoiceActivityListener): Thread {
        val sampleRate = inStream.format.sampleRate.toInt()
        val sampleSize = inStream.format.sampleSizeInBits / 8
        val oneMilliInSamples = sampleRate / 1000

        val minSilenceLength = MIN_SILENCE_MILLIS * oneMilliInSamples
        val minActivityLength = getMinimumVoiceActivityLength(sampleRate)
//        val maxActivityLength =

        val windowSize = WINDOW_MILLIS * oneMilliInSamples
        val correllation = DoubleArray(windowSize)
        val window = DoubleArray(windowSize)
        var position: Int
        var activityStart: Int
        var data = DataInputStream(inStream)
//        var outStream: AudioOutputStream

        val thread = Thread({
            try {
                while (true) {
                    read


                    val mean = bruteForceAutocorrelation(window, correllation)

                    // TODO: JARVIS' MicrophoneAnalyzer implements the algorithms mentiond here - https://github.com/lkuza2/java-speech-ap
                    // https://www.researchgate.net/publication/255667085_A_simple_but_efficient_real-time_voice_activity_detection_algorithm
                    //  - Ignore silence run less than 10 successive frames.
                    //  - Ignore speech run less than 5 successive frames.

                    if (haveEnoughSpeech && haveEnoughSilence) {
                        AudioSystem.write(audioInputStream, type, outputStream)
                        // needs to be 16bit, 16kHz little endian, 1 channel
                        //  ...or audio/x-cbr-opus-with-preamble; preamble-size=0; bit-rate=256000; frame-size-milliseconds=4
                        listener.onVoiceActivity(voiceStream)
                    }
                }
            } catch (e: Exception) {
                logger.info(e.message)
                logger.info("Exiting VAD")
            }
        })
        thread.start()
        return thread
    }

    fun readFrame(dataStream: AudioInputStream): DoubleArray {
        // read one frame's worth of bytes
        val bigEndian = dataStream.format.isBigEndian
        var read: Int
        var totalRead = 0
        val bytesToRead = 3200
        var samplesBuffer = ByteArray(3200)
        val firstSample = totalValuesRead
        try {
            do {
                read = dataStream.read(samplesBuffer, totalRead, bytesToRead - totalRead)
                if (read > 0) {
                    totalRead += read
                }
            } while (read != -1 && totalRead < bytesToRead)
            if (totalRead <= 0) {
                closeDataStream()
                return null
            }
            // shrink incomplete frames
            totalValuesRead += (totalRead / bytesPerValue).toLong()
            if (totalRead < bytesToRead) {
                totalRead = if (totalRead % 2 == 0)
                    totalRead + 2
                else
                    totalRead + 3
                val shrinkedBuffer = ByteArray(totalRead)
                System.arraycopy(samplesBuffer, 0, shrinkedBuffer, 0, totalRead)
                samplesBuffer = shrinkedBuffer
                closeDataStream()
            }
        } catch (ioe: IOException) {
            throw DataProcessingException("Error reading data", ioe)
        }

        // turn it into an Data object
        val doubleData: DoubleArray
        if (bigEndian) {
            doubleData = DataUtil.bytesToValues(samplesBuffer, 0, totalRead, bytesPerValue, signedData)
        } else {
            doubleData = DataUtil.littleEndianBytesToValues(samplesBuffer,0, totalRead, bytesPerValue, signedData)
        }

        return doubleData
    }

    /**
     * Removes silence out of the given voice sample
     * @param voiceSample the voice sample
     * *
     * @return a new voice sample with silence removed
     */
    fun removeSilence(voiceSample: DoubleArray, sampleRate: Int = 16_000): DoubleArray {
        val oneMilliInSamples = sampleRate / 1000

        val length = voiceSample.size
        val minSilenceLength = MIN_SILENCE_MILLIS * oneMilliInSamples
        val minActivityLength = getMinimumVoiceActivityLength(sampleRate)
        val result = BooleanArray(length)

        if (length < minActivityLength) {
            return voiceSample
        }

        val windowSize = WINDOW_MILLIS * oneMilliInSamples
        val correllation = DoubleArray(windowSize)
        val window = DoubleArray(windowSize)

        var position = 0
        while (position + windowSize < length) {
            System.arraycopy(voiceSample, position, window, 0, windowSize)
            val mean = bruteForceAutocorrelation(window, correllation)
            result.fill(mean > threshold, position, position + windowSize)
            position += windowSize
        }

        mergeSmallSilentAreas(result, minSilenceLength)

        val silenceCounter = mergeSmallActiveAreas(result, minActivityLength)

        //        System.out.println((int)((double)silenceCounter / result.length * 100.0d) + "% removed");

        if (silenceCounter > 0) {
            val fadeLength = FADE_MILLIS * oneMilliInSamples
            initFadeFactors(fadeLength)
            val shortenedVoiceSample = DoubleArray(voiceSample.size - silenceCounter)
            var copyCounter = 0
            var i = 0
            while (i < result.size) {
                if (result[i]) {
                    // detect length of active frame
                    val startIndex = i
                    var counter = 0
                    while (i < result.size && result[i++]) {
                        counter++
                    }
                    val endIndex = startIndex + counter

                    applyFadeInFadeOut(voiceSample, fadeLength, startIndex, endIndex)
                    System.arraycopy(voiceSample, startIndex, shortenedVoiceSample, copyCounter, counter)
                    copyCounter += counter
                }
                i++
            }
            return shortenedVoiceSample

        } else {
            return voiceSample
        }
    }

    /**
     * Gets the minimum voice activity length that will be considered by the remove silence method
     * @param sampleRate the sample rate
     * *
     * @return the length
     */
    fun getMinimumVoiceActivityLength(sampleRate: Int): Int {
        return MIN_VOICE_MILLIS * sampleRate / 1000
    }

    fun getMaximumVoiceActivityLength(sampleRate: Int): Int {
        return MAX_VOICE_MILLIS * sampleRate / 1000
    }

    /**
     * Applies a linear fade in / out to the given portion of audio (removes unwanted cracks)
     * @param voiceSample the voice sample
     * *
     * @param fadeLength the fade length
     * *
     * @param startIndex fade in start point
     * *
     * @param endIndex fade out end point
     */
    private fun applyFadeInFadeOut(voiceSample: DoubleArray, fadeLength: Int, startIndex: Int, endIndex: Int) {
        val fadeOutStart = endIndex - fadeLength
        for (j in 0..fadeLength - 1) {
            voiceSample[startIndex + j] *= fadeInFactors!![j]
            voiceSample[fadeOutStart + j] *= fadeOutFactors!![j]
        }
    }

    /**
     * Merges small active areas
     * @param result the voice activity result
     * *
     * @param minActivityLength the minimum length to apply
     * *
     * @return a count of silent elements
     */
    private fun mergeSmallActiveAreas(result: BooleanArray, minActivityLength: Int): Int {
        var active: Boolean
        var increment: Int
        var silenceCounter = 0
        var i = 0
        while (i < result.size) {
            active = result[i]
            increment = 1
            while (i + increment < result.size && result[i + increment] == active) {
                increment++
            }
            if (active && increment < minActivityLength) {
                // convert short activity to opposite
                result.fill(!active, i, i + increment)
                silenceCounter += increment
            }
            if (!active) {
                silenceCounter += increment
            }
            i += increment
        }
        return silenceCounter
    }

    /**
     * Merges small silent areas
     * @param result the voice activity result
     * *
     * @param minSilenceLength the minimum silence length to apply
     */
    private fun mergeSmallSilentAreas(result: BooleanArray, minSilenceLength: Int) {
        var active: Boolean
        var increment: Int
        var i = 0
        while (i < result.size) {
            active = result[i]
            increment = 1
            while (i + increment < result.size && result[i + increment] == active) {
                increment++
            }
            if (!active && increment < minSilenceLength) {
                // convert short silence to opposite
                result.fill(!active, i, i + increment)
            }
            i += increment
        }
    }

    /**
     * Initialize the fade in/ fade out factors properties
     * @param fadeLength
     */
    private fun initFadeFactors(fadeLength: Int) {
        val fadeInFactors = DoubleArray(fadeLength)
        val fadeOutFactors = DoubleArray(fadeLength)
        for (i in 0..fadeLength - 1) {
            fadeInFactors[i] = 1.0 / fadeLength * i
        }
        for (i in 0..fadeLength - 1) {
            fadeOutFactors[i] = 1.0 - fadeInFactors[i]
        }
        this.fadeInFactors = fadeInFactors
        this.fadeOutFactors = fadeOutFactors
    }

    /**
     * Applies autocorrelation in O² operations. Keep arrays very short !
     * @param voiceSample the voice sample buffer
     * *
     * @param correllation the correlation buffer
     * *
     * @return the mean correlation value
     */
    private fun bruteForceAutocorrelation(voiceSample: DoubleArray, correllation: DoubleArray): Double {
        correllation.fill(0.0)
        val n = voiceSample.size
        for (j in 0..n - 1) {
            for (i in 0..n - 1) {
                correllation[j] += voiceSample[i] * voiceSample[(n + i - j) % n]
            }
        }
        val mean = voiceSample.indices.sumByDouble { correllation[it] }
        return mean / correllation.size
    }

    companion object {
        private val logger = Logger.getLogger(CustomMicrophone::class.java.simpleName)
    }
}
