package org.openasr.idiolect.asr.azure

import com.microsoft.cognitiveservices.speech.*
import com.microsoft.cognitiveservices.speech.audio.AudioConfig
import org.openasr.idiolect.asr.AsrProvider
import org.openasr.idiolect.nlp.NlpRequest

/**
 * This is an online service hosted by Azure
 * (or <a href="https://learn.microsoft.com/en-us/azure/cognitive-services/speech-service/speech-container-howto?tabs=stt%2Ccsharp%2Csimple-format">
 *     on-prem containers
 * </a>, but the pricing model still applies)
 *
 * <a href="https://azure.microsoft.com/en-us/pricing/details/cognitive-services/speech-services/#purchase-options">
 *     Pricing Options:
 * </a>
 *
 * <ol>
 *     <li>Free (F0) - 5 audio hours/month</li>
 *     <li>PAYG - $1.481 - 2.073/audio hour</li>
 *     <li>Standard - $1.185/hour (or $0.741 for 50,000 hours/month)
 * </ol>
 */
class AzureAsr : AsrProvider {
//    private lateinit var speechConfig: SpeechConfig
    private lateinit var speechRecognizer: SpeechRecognizer

    override fun displayName() = "Azure"

    override fun activate() {
        val speechConfig = SpeechConfig.fromSubscription(AzureConfig.settings.speechSubscriptionKey, AzureConfig.settings.serviceRegion)
        speechConfig.speechRecognitionLanguage = "en-AU"
        speechConfig.outputFormat = OutputFormat.Simple

        val audioConfig = AudioConfig.fromDefaultMicrophoneInput()
        speechRecognizer = SpeechRecognizer(speechConfig, audioConfig)
    }

    override fun deactivate() {}

    override fun startRecognition() {}

    override fun stopRecognition() {}

    override fun waitForSpeech(): NlpRequest? {
        val task = speechRecognizer.recognizeOnceAsync()
        val speechRecognitionResult: SpeechRecognitionResult = task.get()
        if (speechRecognitionResult.reason == ResultReason.RecognizedSpeech) {
            return NlpRequest(listOf(speechRecognitionResult.text
                .replace(".", "")
                .replace("?", "")
                .lowercase()
            ))
        } else if (speechRecognitionResult.reason == ResultReason.NoMatch) {
            println("NOMATCH: Speech could not be recognized.")
        } else if (speechRecognitionResult.reason == ResultReason.Canceled) {
            val cancellation = CancellationDetails.fromResult(speechRecognitionResult)
            println("CANCELED: Reason=" + cancellation.reason)
            if (cancellation.reason == CancellationReason.Error) {
                println("CANCELED: ErrorCode=" + cancellation.errorCode)
                println("CANCELED: ErrorDetails=" + cancellation.errorDetails)
                println("CANCELED: Did you set the speech resource key and region values?")
            }
        }

        return null
    }
}
