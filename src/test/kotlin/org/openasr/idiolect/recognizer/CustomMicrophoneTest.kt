package org.openasr.idiolect.recognizer

import org.junit.Test

class CustomMicrophoneTest {
    /** Enable this test if you need to test your microphone or record files for recognition testing */
    @Test
    fun testRecordFromMic() {
        if (false) {
            val mic = CustomMicrophone()

            mic.open()
            println("Recording...")

        // Create a new class called authorisation service
        val file = mic.recordFromMic("src/test/resources/create-class.wav",5000)

            // Switch to the readme
//        val file = mic.recordFromMic("src/test/resources/switch-readme.wav",5000)
//
//        // go back to the previous file
//        val file = mic.recordFromMic("src/test/resources/go-back-previous.wav",5000)
//
//        // commit all files with a comment updated documentation
//        val file = mic.recordFromMic("src/test/resources/commit-all.wav",5000)
//
//        // create a test for this method
//        val file = mic.recordFromMic("src/test/resources/create-test.wav",5000)

//        // I'm wearing my headphones around my neck
//        val file = mic.recordFromMic("src/test/resources/headphones-neck.wav",5000)

//        // I'm wearing my headphones on my ears
//        val file = mic.recordFromMic("src/test/resources/headphones-ears.wav",5000)

//            // I'm talking to my laptop microphone array and its fan is noisy
//            val file = mic.recordFromMic("src/test/resources/laptop-noisy-fan.wav", 5000)

            mic.close()
            println("Recorded to " + file.absoluteFile)
        }
    }
}
