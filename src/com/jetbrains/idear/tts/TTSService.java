package com.jetbrains.idear.tts;

public interface TTSService {
    void init();

    void say(String text);

    void dispose();
}
