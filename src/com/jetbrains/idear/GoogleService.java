package com.jetbrains.idear;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;

/**
 * Created by breandan on 7/11/2015.
 */
public class GoogleService {
    private static final String API_KEY = "AIzaSyB0DS1X9_qkZw2keZWw9p9EwUb9cV2bYsw";
    private static final String URL_PRE = "https://www.google.com/speech-api/v2/recognize";
    private static final String URL_SEARCH = "https://www.google.com/search?q=";

    public String getTextForLastUtterance() {
        try {
            HttpResponse<JsonNode> jsonResponse = Unirest.post(URL_PRE)
                    .header("accept", "application/json")
                    .queryString("output", "json")
                    .queryString("lang", "en-us")
                    .queryString("key", API_KEY)
                    .header("Content-Type", "audio/l16; rate=16000;")
                    .body(Files.readAllBytes(getLastRecordedUtterance().toPath()))
                    .asJson();

            return jsonResponse.getBody().toString();

        } catch (IOException | UnirestException e) {
            e.printStackTrace();
        }

        return "";
    }

    public void searchGoogle(String s) {
        try {
            Desktop.getDesktop().browse(java.net.URI.create(URL_SEARCH + URLEncoder.encode(s, "UTF-8")));
        }
        catch (java.io.IOException e) {
            e.printStackTrace();
        }
    }

    public void textToSpeech(String s) {
        // TODO
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
        System.out.println(new GoogleService().getTextForLastUtterance());
    }
}
