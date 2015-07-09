package com.jetbrains.idear;

import marytts.LocalMaryInterface;
import marytts.MaryInterface;
import marytts.exceptions.MaryConfigurationException;
import marytts.exceptions.SynthesisException;
import marytts.modules.synthesis.Voice;
import marytts.util.data.audio.AudioPlayer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sound.sampled.AudioInputStream;
import java.util.Locale;
import java.util.Scanner;

/**
 * Created by breandan on 7/9/2015.
 */
public class TTSService {
    private static final Logger logger = LoggerFactory.getLogger(TTSService.class);
    private static MaryInterface maryTTS;
    private Voice voice;

    public TTSService() {
        try {
            maryTTS = new LocalMaryInterface();
            Locale systemLocale = Locale.getDefault();
            if (maryTTS.getAvailableLocales().contains(systemLocale)) {
                voice = Voice.getDefaultVoice(systemLocale);
            }

            if (voice == null) {
                voice = Voice.getVoice(maryTTS.getAvailableVoices().iterator().next());
            }

            maryTTS.setLocale(voice.getLocale());
            maryTTS.setVoice(voice.getName());
        } catch (MaryConfigurationException e) {
            logger.error(e.getLocalizedMessage(), e);
        }
    }

    public void say(String text) {
        if (text == null || text.isEmpty()) {
            return;
        }

        try {
            AudioInputStream audio = maryTTS.generateAudio(text);
            AudioPlayer player = new AudioPlayer(audio);
            player.start();
            player.join();
        } catch (SynthesisException e) {
            logger.error(e.getLocalizedMessage(), e);
        } catch (InterruptedException e) {
            logger.error(e.getLocalizedMessage(), e);
        }
    }

    public static void main(String[] args) {
        TTSService ttService = new TTSService();
        Scanner scan = new Scanner(System.in);

        while (true) {
            System.out.println("Text to speak:");
            ttService.say(scan.nextLine());
        }
    }
}
