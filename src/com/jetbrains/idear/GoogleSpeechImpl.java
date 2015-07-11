package com.jetbrains.idear;

/**
 * Created by breandan on 7/11/2015.
 */
public class GoogleSpeechImpl implements GoogleService {
    private static final String API_KEY = "AIzaSyB0DS1X9_qkZw2keZWw9p9EwUb9cV2bYsw";
    private static final String URL_PRE = "https://www.google.com/speech-api/v2/recognize?output=json&lang=en-us&key=";

    @Override
    public String getTextForLastUtterance() {
        return null;
    }

    @Override
    public void searchGoogle(String s) {

    }
}
