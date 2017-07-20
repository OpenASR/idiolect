package org.openasr.idear.tts

import com.amazonaws.services.polly.AmazonPollyClientBuilder
import com.amazonaws.services.polly.model.*
import javazoom.jl.player.FactoryRegistry.systemRegistry
import javazoom.jl.player.advanced.*
import org.openasr.idear.recognizer.awslex.AwsUtils
import java.io.InputStream
import java.util.*

/**
 * Either:
 * <ul>
 *     <li>Users define <code>AWS_ACCESS_KEY_ID</code> (or <code>AWS_ACCESS_KEY</code>) and
 *          <code>AWS_SECRET_KEY</code> (or <code>AWS_SECRET_ACCESS_KEY</code>)</li>
 *     <li>the plugin must provide <code>aws.accessKeyId</code> and <code>aws.secretKey</code></li>
 *     <li>(not implemented) the plugin could use STS or Cognito</li>
 * </ul>
 *
 * @see http://docs.aws.amazon.com/polly/latest/dg/examples-java.html
 */

object PollyTTS : TTSProvider {
    private val polly = AmazonPollyClientBuilder.standard().apply {
        region = AwsUtils.REGION
        credentials = AwsUtils.credentialsProvider
    }.build()

    private var voice = polly.describeVoices(DescribeVoicesRequest()).voices[0]

    override fun say(utterance: String) =
            utterance.let {
                val audio = synthesize(it, OutputFormat.Mp3)
                if (audio != null) {
                    playAudio(audio)
                    true
                } else false
            }

    override fun dispose() = Unit

    private fun synthesize(text: String, format: OutputFormat): InputStream? {
        val synthReq = SynthesizeSpeechRequest().withText(text).withVoiceId(voice.id).withOutputFormat(format)
        val synthRes = polly.synthesizeSpeech(synthReq)

        return synthRes?.audioStream
    }

    private fun playAudio(inputStream: InputStream) {
        //create an MP3 player
        val player = AdvancedPlayer(inputStream, systemRegistry().createAudioDevice())

        player.playBackListener = object : PlaybackListener() {
            override fun playbackStarted(evt: PlaybackEvent?) = println("Playback started")

            override fun playbackFinished(evt: PlaybackEvent?) = println("Playback finished")
        }

        // play it!
        player.play()
    }
}

fun main(args: Array<String>) {
    val ttService = PollyTTS
    val scan = Scanner(System.`in`)

    while (true) {
        println("Text to speak:")
        ttService.say(scan.nextLine())
    }
}