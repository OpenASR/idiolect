package org.openasr.idiolect.recognizer

import javax.sound.sampled.AudioInputStream
import javax.sound.sampled.TargetDataLine

class VoiceActivityStream(line: TargetDataLine,
                          threshold: Short = 100,
                          private val vad: VAD = VAD(threshold),
                          minSpeechMills: Int = 100,
                          maxSpeechMills: Int = 0, // 10_000,
                          maxSilenceMills: Int = 1000) : AudioInputStream(line) {
    private val DEFAULT_MASTER_GAIN = 1.0
    private var state: StreamState = StreamState.SILENCE
    private val minSpeech = line.format.sampleRate * minSpeechMills / 1000
    private val maxSpeech = line.format.sampleRate * maxSpeechMills / 1000
    private val maxSilence = line.format.sampleRate * maxSilenceMills / 1000
    private var silenceTime: Int = 0
    private var speechTime: Int = 0
    private var masterGain = DEFAULT_MASTER_GAIN
    private val frameSize = 1024

    private enum class StreamState {
        SPEECH, SILENCE
    }

    fun setMasterGain(gain: Double) {
        masterGain = gain
    }

    fun setNoiseLevel(threshold: Double) {
        vad.setThreshold(threshold.toInt().toShort())
    }

    override fun read(b: ByteArray, off: Int, len: Int): Int {
        val frameBytes = ByteArray(frameSize shl 1)
        val frame = ShortArray(frameSize)
        var processedBytes = 0
        state = StreamState.SILENCE
        silenceTime = 0
        speechTime = 0

        while (true) {
            val bytesRead = super.read(frameBytes, 0, (frameSize shl 1).coerceAtMost(len - processedBytes))

            if (bytesRead <= 0) {
                return processedBytes
            }

            for ((j, i) in (0 until bytesRead step 2).withIndex()) {
                var sample = ((frameBytes[i + 1].toInt() shl 8) or (frameBytes[i].toInt() and 0xFF)).toShort()

//                if (sample < noiseLevel && sample > -noiseLevel) {
//                    sample = 0
//                }
                sample = (sample * masterGain).toInt().toShort()  // .coerceIn(-32768, 32767)

                frame[j] = sample
            }

            if (vad.isSpeech(frame)) {
                if (state == StreamState.SILENCE) {
                    // end of silence
                    silenceTime = 0
                    state = StreamState.SPEECH
                }
                speechTime += bytesRead shr 1
            } else { // silence
                silenceTime += bytesRead shr 1

                if (state == StreamState.SPEECH) {
                    // silence within speech
                    state = StreamState.SILENCE

                    if (silenceTime >= maxSilence) {
                        // found end of speech
                        if (speechTime < minSpeech) {
                            // not enough speech, reset & continue
                            speechTime = 0
                            processedBytes = 0
                            continue
                        }
                        // otherwise if we have > minSpeech & processedByte break
                    }
                }
            }

            if (state == StreamState.SPEECH) {
                for (i in 0 until bytesRead step 2) {
                    b[processedBytes++] = frameBytes[i]
                    b[processedBytes++] = frameBytes[i + 1]
                }
                if (maxSpeech > 0 && speechTime >= maxSpeech) {
                    break
                }
            } else if (speechTime >= minSpeech && silenceTime >= maxSilence) {
                break
            }
        }

        return processedBytes
    }

//    fun readShorts(b: ShortArray, off: Int, len: Int): Int {
//    }
//
//    fun readFloats(b: FloatArray, off: Int, len: Int): Int {
//    }
}
