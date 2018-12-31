package com.darkprograms.speech.encoding

import net.sourceforge.javaflacencoder.*
import java.io.File
import java.nio.*
import javax.sound.sampled.AudioSystem

/*************************************************************************************************************
 * Class that contains methods to encode Wave files to FLAC files
 * THIS IS THANKS TO THE javaFlacEncoder Project created here: http://sourceforge.net/projects/javaflacencoder/
 */
/**
 * Constructor
 */
class FlacEncoder {

    /**
     * Converts a wave file to a FLAC file(in order to POST the data to Google and retrieve a response) <br></br>
     * Sample Rate is 8000 by default
     *
     * @param inputFile  Input wave file
     * @param outputFile Output FLAC file
     */
    fun convertWaveToFlac(inputFile: File, outputFile: File) {
        val streamConfiguration = StreamConfiguration()
        streamConfiguration.sampleRate = 8000
        streamConfiguration.bitsPerSample = 16
        streamConfiguration.channelCount = 1


        try {
            val audioInputStream = AudioSystem.getAudioInputStream(inputFile)
            val format = audioInputStream.format

            val frameSize = format.frameSize

            val flacEncoder = FLACEncoder()
            val flacOutputStream = FLACFileOutputStream(outputFile)

            flacEncoder.setStreamConfiguration(streamConfiguration)
            flacEncoder.setOutputStream(flacOutputStream)

            flacEncoder.openFLACStream()

            var frameLength = audioInputStream.frameLength.toInt()
            if (frameLength <= AudioSystem.NOT_SPECIFIED) {
                frameLength = 16384//Arbitrary file size
            }
            var sampleData = IntArray(frameLength)
            val samplesIn = ByteArray(frameSize)

            var i = 0

            while (audioInputStream.read(samplesIn, 0, frameSize) != -1) {
                if (frameSize != 1) {
                    val bb = ByteBuffer.wrap(samplesIn)
                    bb.order(ByteOrder.LITTLE_ENDIAN)
                    val shortVal = bb.short
                    sampleData[i] = shortVal.toInt()
                } else {
                    sampleData[i] = samplesIn[0].toInt()
                }

                i++
            }

            sampleData = truncateNullData(sampleData, i)

            flacEncoder.addSamples(sampleData, i)
            flacEncoder.encodeSamples(i, false)
            flacEncoder.encodeSamples(flacEncoder.samplesAvailableToEncode(), true)

            audioInputStream.close()
            flacOutputStream.close()

        } catch (ex: Exception) {
            ex.printStackTrace()
        }

    }


    /**
     * Converts a wave file to a FLAC file(in order to POST the data to Google and retrieve a response) <br></br>
     * Sample Rate is 8000 by default
     *
     * @param inputFile  Input wave file
     * @param outputFile Output FLAC file
     */
    fun convertWaveToFlac(inputFile: String, outputFile: String) {
        convertWaveToFlac(File(inputFile), File(outputFile))
    }

    /**
     * Used for when the frame length is unknown to shorten the array to prevent huge blank end space
     * @param sampleData The int[] array you want to shorten
     * @param index The index you want to shorten it to
     * @return The shortened array
     */
    private fun truncateNullData(sampleData: IntArray, index: Int): IntArray {
        if (index == sampleData.size) return sampleData
        val out = IntArray(index)
        System.arraycopy(sampleData, 0, out, 0, index)
        return out
    }

}
