package org.openasr.idiolect.tts

import io.github.jonelo.jAdapterForNativeTTS.engines.*
import io.github.jonelo.jAdapterForNativeTTS.engines.exceptions.NotSupportedOperatingSystemException
import java.io.IOException


fun main() {
    val text = "Hello idea!"
    try {
        val speechEngine = SpeechEngineNative.getInstance()
        val voices = speechEngine.availableVoices
        if (voices.isNotEmpty()) {
            println("For now the following voices are supported:\n")
            for (voice in voices) {
                System.out.printf("%s\n", voice)
            }
            // We want to find a voice according our preferences
            val voicePreferences = VoicePreferences()
            voicePreferences.language = "en" //  ISO-639-1
            voicePreferences.country = "AU" // ISO 3166-1 Alpha-2 code
            voicePreferences.gender = VoicePreferences.Gender.MALE
            println("cultures: ${voices.map { it.culture }}")

            var voice: Voice? = voices.first { "US" in it.culture }
            // simple fallback just in case our preferences didn't match any voice
            if (voice == null) {
                System.out.printf("Warning: Voice has not been found by the voice preferences %s\n", voicePreferences)
                voice = voices[0]
                System.out.printf("Using \"%s\" instead.\n", voice)
            }
            speechEngine.setVoice(voice?.name)
            speechEngine.say(text)
        } else {
            System.out.printf("Error: not even one voice have been found.\n")
        }
    } catch (e: NotSupportedOperatingSystemException) {
        System.err.println(e.message)
    } catch (e: IOException) {
        System.err.println(e.message)
    }
}
