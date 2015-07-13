package com.jetbrains.idear.tts;

import marytts.LocalMaryInterface;
import marytts.MaryInterface;
import marytts.exceptions.MaryConfigurationException;
import marytts.exceptions.SynthesisException;
import marytts.modules.synthesis.Voice;
import marytts.util.data.audio.AudioPlayer;

import javax.sound.sampled.AudioInputStream;
import java.util.Locale;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by breandan on 7/9/2015.
 */
public class TTSServiceImpl implements TTSService {
    private static final Logger logger = Logger.getLogger(TTSServiceImpl.class.getSimpleName());
    private static MaryInterface maryTTS;
    private Voice voice;

    public TTSServiceImpl() {
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
            e.printStackTrace();
        }
    }

    @Override
    public void init() {

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
        } catch (SynthesisException | InterruptedException e) {
            logger.log(Level.SEVERE, e.getLocalizedMessage(), e);
        }
    }

    @Override
    public void dispose() {
        // TODO
    }

    public static void main(String[] args) {
        TTSServiceImpl ttService = new TTSServiceImpl();
        ttService.init();
        Scanner scan = new Scanner(System.in);

        while (true) {
            System.out.println("Text to speak:");
            ttService.say(scan.nextLine());
        }
    }
}
