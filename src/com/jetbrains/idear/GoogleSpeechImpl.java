package com.jetbrains.idear;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;

/**
 * Created by breandan on 7/11/2015.
 */
public class GoogleSpeechImpl implements GoogleService {
    private static final String API_KEY = "AIzaSyB0DS1X9_qkZw2keZWw9p9EwUb9cV2bYsw";
    private static final String URL_PRE = "https://www.google.com/speech-api/v2/recognize?output=json&lang=en-us&key=";

    @Override
    public String getTextForLastUtterance() {

        return "";
    }

    @Override
    public void searchGoogle(String s) {

    }

    @Override
    public void textToSpeech(String s) {

    }

    private static File getLastRecordedUtterance() {
        File dir = new File(System.getProperty("user.dir"));
        File lastRecorded = dir;
        FileTime bestTime = FileTime.fromMillis(0);

        for (File f : dir.listFiles()) {
            if (f.getName().endsWith("wav")) {
                FileTime currentTime = getTimeCreated(f);
                if (currentTime.compareTo(bestTime) > 0) {
                    lastRecorded = f;
                    bestTime = currentTime;
                }
            }
        }

        return lastRecorded;
    }

    private static FileTime getTimeCreated(File f) {
        try {
            return Files.readAttributes(f.toPath(), BasicFileAttributes.class).creationTime();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return FileTime.fromMillis(0);
    }

    public static void main(String[] args) {
        System.out.println(getLastRecordedUtterance());
    }
}
