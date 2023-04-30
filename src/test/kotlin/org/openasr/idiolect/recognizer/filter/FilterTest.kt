package org.openasr.idiolect.recognizer.filter

import io.mockk.*
import org.assertj.core.api.Assertions.*
import org.junit.Assert
import org.junit.Test
import org.openasr.idiolect.recognizer.AudioInputStreamWithNoiseSuppression
import org.openasr.idiolect.recognizer.CustomMicrophone
import org.openasr.idiolect.utils.AudioUtils
import org.openasr.idiolect.utils.TestAudioUtils
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import javax.sound.sampled.AudioFormat
import javax.sound.sampled.TargetDataLine


class FilterTest {
    private val testFile = "talking-to-noisy-laptop"
//    private val testFile = "laptop-noise" // TestAudioUtils will append ".wav"
    private var laptopNoiseCoefficients = doubleArrayOf(
        -0.000096, -0.91476, 0.21692, 0.637131, -0.882325, 0.629477, 0.732128, 0.14931, 0.683021, -0.449947,
        -0.195725, -0.384524, 0.602963, -0.27877, -0.007903, 0.922186, -0.041942, -0.957236, -0.10331, -0.074716,
        0.762219, 0.078101, -0.668467, -0.077726, 0.232278, -0.19471, -0.112478, 0.856841, -0.805704, -0.190218,
        -0.888645, 0.085636, -0.052513, -0.984151, 0.111289, -0.913696, -0.466431, -0.116557, 0.562486, 0.097048,
        0.548813, -0.110109, -0.195098, -0.648148, -0.611994, -0.92909, 0.009837, -0.688186, -0.875994, -0.587024,
        -0.763014, -0.908129, -0.896479, 0.969204, 0.612043, 0.143521, -0.220817, 0.713425, 0.825597, -0.822016,
        -0.700049, -0.64774, 0.687256, 0.300556, -0.053656, 0.582557, 0.564671, -0.624245, -0.37607, 0.611629,
        0.195302, -0.245815, 0.31796, -0.84968, -0.420518, -0.33415, -0.335984, 0.543993, -0.553459, -0.178986,
        -0.396055, -0.775872, 0.131459, -0.941505, -0.526652, -0.756721, 0.607705, 0.24264, -0.193668, -0.701279,
        0.89264, 0.03403, -0.354836, 0.355214, -0.924947, 0.645312, 0.206965, 0.744856, 0.810105, -0.757627
    )


    @Test
    fun testNoiseSuppression() {
        // Given
        val audioInputStream = TestAudioUtils.getInputStream(testFile)
        val format = CustomMicrophone.format
        val line = mockk<TargetDataLine>(relaxed = true)
        every { line.format } returns format

        val noiseSuppression = AudioInputStreamWithNoiseSuppression(line)

        val allBytes = audioInputStream.readAllBytes()

        // When
        noiseSuppression.applySuppression(allBytes, 0, allBytes.size)
        val filteredStream = ByteArrayOutputStream(allBytes.size)
        filteredStream.writeBytes(allBytes)

        // Then
        assertFilteredOutput(filteredStream, audioInputStream.format, "noise-suppression")
    }

    @Test
    fun testActiveNoiseCancellingTraining() {
        // Given
        val audioInputStream = TestAudioUtils.getInputStream("laptop-noise")
        val hzPerSample = Filter.calculateHzPerSample(audioInputStream.format.sampleRate, audioInputStream.format.sampleSizeInBits)
        val cutoffFrequency = 100
        val filter = ActiveNoiseCancelling(cutoffFrequency, hzPerSample)
        // .2 is good
        // 0.175 diverges early
        filter.setStepSize(0.15)             // 0.15
        filter.setConvergenceFactor(0.0000021)  // 0.0002

        // When
        val coefficients = filter.trainCoefficients(audioInputStream.readAllBytes())

        // Then
        laptopNoiseCoefficients = coefficients
        println("coefficients: ${coefficients.map { c -> c.toString() }}")
    }

    @Test
    fun testActiveNoiseCancellingTrainingHasNonZeroCoefficientsAfter0() {
        val cutoffFrequency = 1000
        val sampleRate = 16000
        val taps = 100
        val filter = ActiveNoiseCancelling(cutoffFrequency, sampleRate, taps)
        // Create an array of random Shorts between -200 and 199
        val noiseLevel = 1000
        val randomShorts = ShortArray(sampleRate) { ((Math.random() * noiseLevel * 2 - noiseLevel).toInt() and 0xffff).toShort() }
//        val randomShorts = ShortArray(sampleRate) { 200 }

        // Convert the Shorts to bytes in little-endian order
        val audioData = ByteArray(sampleRate * 2) { i ->
            if (i % 2 == 0) randomShorts[i / 2].toByte()
            else (randomShorts[i / 2].toInt() shr 8).toByte()
        }
//        val audioData = ByteArray(sampleRate * 2) { 0 }
        val coefficients = filter.trainCoefficients(audioData)

        // Check that the coefficients are not all zero
        var foundNonZeroCoefficient = false
        for (i in 1 until coefficients.size) {
            if (coefficients[i] != 0.0) {
                foundNonZeroCoefficient = true
                break
            }
        }
        assertThat(foundNonZeroCoefficient).isTrue()
    }

    @Test
    fun testActiveNoiseCancelling() {
        testActiveNoiseCancellingTraining()

        // Given
        val audioInputStream = TestAudioUtils.getInputStream(testFile)
        val hzPerSample = Filter.calculateHzPerSample(audioInputStream.format.sampleRate, audioInputStream.format.sampleSizeInBits)
        val cutoffFrequency = 100
        val filter = ActiveNoiseCancelling(cutoffFrequency, hzPerSample, 100, laptopNoiseCoefficients)

        // When
        val filteredStream = filter.filter(audioInputStream)

        // Then
        assertFilteredOutput(filteredStream, audioInputStream.format, "anc")
    }

    @Test
    fun testHighPassFilter() {
        // Given
        val audioInputStream = TestAudioUtils.getInputStream(testFile)
        val hzPerSample = Filter.calculateHzPerSample(audioInputStream.format.sampleRate, audioInputStream.format.sampleSizeInBits)
        val cutoffFrequency = 80
        val filter = FirHighPassFilter(cutoffFrequency, hzPerSample)

        // When
        val filteredStream = filter.filter(audioInputStream)

        // Then
        assertFilteredOutput(filteredStream, audioInputStream.format, "high-pass")
    }

    @Test
    fun testLowPassFilter() {
        // Given
        val audioInputStream = TestAudioUtils.getInputStream(testFile)
        val hzPerSample = Filter.calculateHzPerSample(audioInputStream.format.sampleRate, audioInputStream.format.sampleSizeInBits)
        val cutoffFrequency = 2
//        val filter = LowPassFilter(cutoffFrequency, hzPerSample)
        val filter = FirLowPassFilter(cutoffFrequency, audioInputStream.format.sampleRate.toInt(), 500)

        // When
        val filteredStream = filter.filter(audioInputStream)

        // Then
        assertFilteredOutput(filteredStream, audioInputStream.format, "low-pass")
    }

    @Test
    fun testNotchFilter() {
        // Given
        val audioInputStream = TestAudioUtils.getInputStream(testFile)
        val format = audioInputStream.format
        val notchFrequency = 215
        val notchWidth = 30
//        val notchFrequency = 280
//        val notchWidth = 40
        val filter = NotchFilter(format, format.sampleRate, notchFrequency, notchWidth)

        // When
        val filteredStream = filter.filter(audioInputStream)

        // Then
        assertFilteredOutput(filteredStream, audioInputStream.format, "notch")
    }

    private fun assertFilteredOutput(filteredAudio: ByteArrayOutputStream, format: AudioFormat, filterType: String) {
        val outputFile = "$testFile-$filterType"

        // uncomment to test other files
        TestAudioUtils.saveAudioStream(filteredAudio, format, outputFile)

        val expectedAudioInputStream = TestAudioUtils.getInputStream(outputFile)
//        assertThat(filteredAudio).usingRecursiveComparison().isEqualTo(expectedAudioInputStream)
    }
}
