package org.openasr.idiolect.presentation

import io.mockk.every
import io.mockk.mockk
import org.junit.Assert
import org.junit.Test
import org.openasr.idiolect.presentation.toolwindow.audio.VuMeter
import org.openasr.idiolect.recognizer.CustomMicrophone
import javax.sound.sampled.TargetDataLine

class VuMeterTest {
    private val microphone = CustomMicrophone()

    class TestableVuMeter(microphone: CustomMicrophone, mode: Int = MAX_MODE) : VuMeter(microphone, mode) {
        override fun setValue(value: Int) {
            super.setValue(value)
            stop()
        }
    }

    @Test
    fun testCalculateMaxLevel() {
        // Given
        prepareDataLine(0, 100, -3000, 60)
        val vuMeter = TestableVuMeter(microphone)

        // When
        vuMeter.run()

        // Then
        Assert.assertEquals(3000, vuMeter.value)
    }

    @Test
    fun testCalculateRmsLevel() {
        // Given
        prepareDataLine(0, 100, -3000, 60)
        val vuMeter = TestableVuMeter(microphone, VuMeter.RMS_MODE)

        // When
        vuMeter.run()

        // Then
        println("RMS: " + vuMeter.value)
//        Assert.assertEquals(15216, vuMeter.value)
    }

    @Test
    fun testCalculateRmsLevelLow() {
        // Given
        prepareDataLine(0, 0, 0, 0)
        val vuMeter = TestableVuMeter(microphone, VuMeter.RMS_MODE)

        // When
        vuMeter.run()

        // Then
        Assert.assertEquals(0, vuMeter.value)
    }

    @Test
    fun testCalculateRmsLevelMid() {
        // Given
        prepareDataLine(16383, 16383, 16383, 16383)
        val vuMeter = TestableVuMeter(microphone, VuMeter.RMS_MODE)

        // When
        vuMeter.run()

        // Then
        println("RMS mid: " + vuMeter.value)
//        Assert.assertEquals(28821, vuMeter.value)
    }

    @Test
    fun testCalculateRmsLevelHigh() {
        // Given
        prepareDataLine(Short.MAX_VALUE, Short.MAX_VALUE, Short.MAX_VALUE, Short.MAX_VALUE)
        val vuMeter = TestableVuMeter(microphone, VuMeter.RMS_MODE)

        // When
        vuMeter.run()

        // Then
        Assert.assertEquals(Short.MAX_VALUE.toInt(), vuMeter.value)
    }

    private fun prepareDataLine(vararg data: Short) {
        val dataLine = mockk<TargetDataLine>(relaxed = true)
        every { dataLine.bufferSize } returns data.size * 2 * 5

        every { dataLine.read(any(), any(), any()) } answers {
            val buffer = firstArg<ByteArray>()

            for (i in data.indices) {
                writeShortLE(buffer, i * 2, data[i])
            }

            thirdArg()
        }

        microphone.useLine(dataLine)
    }


    private fun writeShortLE(buffer: ByteArray, offset: Int, value: Short) {
        buffer[offset] = value.toByte()
        buffer[offset + 1] = value.toInt().shr(8).toByte()
    }
}
