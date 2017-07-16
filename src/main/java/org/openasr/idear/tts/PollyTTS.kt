package org.openasr.idear.tts

import com.amazonaws.services.polly.AmazonPolly
import com.amazonaws.services.polly.AmazonPollyClientBuilder
import com.amazonaws.services.polly.model.DescribeVoicesRequest
import com.amazonaws.services.polly.model.OutputFormat
import com.amazonaws.services.polly.model.SynthesizeSpeechRequest
import com.amazonaws.services.polly.model.Voice
import javazoom.jl.player.advanced.AdvancedPlayer
import javazoom.jl.player.advanced.PlaybackEvent
import javazoom.jl.player.advanced.PlaybackListener
import org.openasr.idear.recognizer.awslex.AwsUtils
import java.io.IOException
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

class PollyTTS : TTSProvider {
    val polly: AmazonPolly = AmazonPollyClientBuilder.standard().apply {
        region = AwsUtils.REGION
        credentials = AwsUtils.credentialsProvider
    }.build()
    private var voice: Voice = polly.describeVoices(DescribeVoicesRequest()).voices[0]

    override fun say(text: String?) {
        text?.let {
            val audio = synthesize(it, OutputFormat.Mp3)
            if (audio != null) {
                playAudio(audio)
            }
        }
    }

    override fun dispose() { }

    @Throws(IOException::class)
    private fun synthesize(text: String, format: OutputFormat): InputStream? {
        val synthReq = SynthesizeSpeechRequest().withText(text).withVoiceId(voice.id).withOutputFormat(format)
        val synthRes = polly.synthesizeSpeech(synthReq)

        return synthRes?.audioStream
    }

    private fun playAudio(inputStream: InputStream) {
        //create an MP3 player
        val player = AdvancedPlayer(inputStream,
                javazoom.jl.player.FactoryRegistry.systemRegistry().createAudioDevice())

        player.playBackListener = object : PlaybackListener() {
            override fun playbackStarted(evt: PlaybackEvent?) {
                println("Playback started")
            }

            override fun playbackFinished(evt: PlaybackEvent?) {
                println("Playback finished")
            }
        }

        // play it!
        player.play()

        /* Borrowed from http://blog.conygre.com/2016/12/06/at-the-third-stroke-the-time-will-be-spoken-by-aws-polly/
        ...but it didn't work

        val audio = AudioSystem.getAudioInputStream(BufferedInputStream(inputStream))
        val format = getOutFormat(audio.format)
        val info = DataLine.Info(SourceDataLine::class.java, format)

        val line = AudioSystem.getLine(info) as SourceDataLine
        if (line != null) {
            line!!.open(format)
            line!!.start()
            stream(AudioSystem.getAudioInputStream(format, audio), line)
            line!!.drain()
            line!!.stop()
        }*/
    }

    /*private fun getOutFormat(inFormat: AudioFormat): AudioFormat {
        val ch = inFormat.channels
        val rate = inFormat.sampleRate
        return AudioFormat(PCM_SIGNED, rate, 16, ch, ch * 2, rate, false)
    }

    @Throws(IOException::class)
    private fun stream(`in`: AudioInputStream, line: SourceDataLine) {
        val buffer = ByteArray(65536)
        var n = 0
        while (n != -1) {
            line.write(buffer, 0, n)
            n = `in`.read(buffer, 0, buffer.size)
        }
    }*/
}

fun main(args: Array<String>) {
    val ttService = PollyTTS()
    val scan = Scanner(System.`in`)

    while (true) {
        println("Text to speak:")
        ttService.say(scan.nextLine())
    }
}