package com.darkprograms.speech.recognizer.vad

import com.darkprograms.speech.microphone.MicrophoneAnalyzer

/**
 * Implementation of [https://www.researchgate.net/publication/255667085_A_simple_but_efficient_real-time_voice_activity_detection_algorithm]
 *
 * !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
 * !! WARNING - this is not working correctly !!
 * !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
 *
 * TODO: need to calculate Spectral Flatness Measure
 */
class MoattarHomayounpourVAD : AbstractVAD() {

    private var minEnergy = Integer.MAX_VALUE
    private var minFrequency = Integer.MAX_VALUE
    private var minSpectralFlatness = Integer.MAX_VALUE

    override fun run() {
        minEnergy = Integer.MAX_VALUE
        minFrequency = Integer.MAX_VALUE
        minSpectralFlatness = Integer.MAX_VALUE
        super.run()
    }

    override fun sampleForSpeech(audioData: ByteArray): Boolean {
        var counter = 0
        val energy = MicrophoneAnalyzer.calculateRMSLevel(audioData)
        val frequency = mic!!.getFrequency(audioData)

        // ignore frequencies above 400hz (and below 50Hz?)
        if (frequency < 400) {
            //     3-2-2- Compute the abstract value of Spectral Flatness Measure SFM(i)
            // TODO        https://github.com/filipeuva/SoundBites/blob/master/src/uk/co/biogen/SoundBites/analysis/AnalysisInterface.java#L264

            //   3-3- Supposing that some of the first 30 frames are silence, find the minimum value for E, F & SF
            minEnergy = Math.min(minEnergy, energy)
            minFrequency = Math.min(minFrequency, frequency)
            //                minSpectralFlatness = Math.min(minSpectralFlatness, energy);

            val energyThreshold = ENERGY_PRIMARY_THRESHOLD * Math.log(minEnergy.toDouble())
            println("energy: $energy\tfrequency:$frequency")
            if (energy - minEnergy >= energyThreshold) counter++
            if (frequency - minFrequency >= FREQUENCY_PRIMARY_THRESHOLD) counter++
            //                if (sfm - minSpectralFlatness) >= SPECTRAL_FLATNESS_PRIMARY_THRESHOLD) counter++;
        }

        if (counter > 1) {
            return true
        } else {
            minEnergy = (silenceCount * minEnergy + energy) / (silenceCount + 1)
            return false
        }
    }

    companion object {
        private val ENERGY_PRIMARY_THRESHOLD = 40
        private val FREQUENCY_PRIMARY_THRESHOLD = 185
        private val SPECTRAL_FLATNESS_PRIMARY_THRESHOLD = 5
    }
}
