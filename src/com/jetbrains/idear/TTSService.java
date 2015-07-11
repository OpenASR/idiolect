package com.jetbrains.idear;

public interface TTSService {
    void init();

    void say(String text);

    void dispose();
}
