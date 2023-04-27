package org.openasr.idiolect.recognizer.filter

import io.mockk.*
import org.assertj.core.api.Assertions.*
import org.junit.Assert
import org.junit.Test
import org.openasr.idiolect.utils.AudioUtils
import org.openasr.idiolect.utils.TestAudioUtils
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import javax.sound.sampled.AudioFormat


class FilterTest {
    private val testFile = "create-class" // TestAudioUtils will append ".wav"

    @Test
    fun testFilterInputStream() {
        val b = byteArrayOf(0x40, 0x14, 0x0BB.toByte(), 0x11)
        val hzPerSample = 8000
        val cutoffFrequency = 1000
        val inputStream = ByteArrayInputStream(b)
        // Mock filter to verify filterSample(b, 0) input
//        val filter = mockk<LowPassFilter>()
        val filter = spyk(LowPassFilter(cutoffFrequency, hzPerSample))
        val capturedSamples = mutableListOf<Int>()

//        every { filter getProperty "cutoffFrequency" } returns cutoffFrequency
//        every { filter getProperty "hzPerSample" } returns 125.0
//        every { filter.property } returns cutoffFrequency
//        every { filter getProperty "hzPerSample" } returns 125.0
//        every { filter.filter(inputStream) } answers { callOriginal() }
//        every { filter.filterSample(any(), any()) } answers { callOriginal() }
        every { filter.filterSample(sample = capture(capturedSamples)) } answers { callOriginal() }

        // When
        val filteredStream = filter.filter(inputStream)

        // Then
        assertThat(capturedSamples[0]).isEqualTo(5184)
        assertThat(capturedSamples[1]).isEqualTo(4539)

        val filterdBytes = filteredStream.toByteArray()
        val filteredSamples = mutableListOf<Short>()
        AudioUtils.readLittleEndianShorts(filterdBytes, b.size) { sample -> filteredSamples.add(sample) }
        assertThat(filteredSamples[0]).isEqualTo(4667)
        assertThat(filteredSamples[1]).isEqualTo(4086)
    }

    @Test
    fun testHighPassFilter() {
        // Given
        val audioInputStream = TestAudioUtils.getInputStream(testFile)
        val hzPerSample = Filter.calculateHzPerSample(audioInputStream.format.sampleRate, audioInputStream.format.sampleSizeInBits)
        val cutoffFrequency = 80
        val filter = HighPassFilter(cutoffFrequency, hzPerSample)

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
        val cutoffFrequency = 1000
        val filter = LowPassFilter(cutoffFrequency, hzPerSample)

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
