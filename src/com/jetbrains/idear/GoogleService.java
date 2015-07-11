package com.jetbrains.idear;

import com.intellij.openapi.util.Pair;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import java.awt.*;
import java.io.*;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GoogleService {

    private Logger logger = Logger.getLogger(GoogleService.class.getSimpleName());

    private static final String API_KEY = "AIzaSyB0DS1X9_qkZw2keZWw9p9EwUb9cV2bYsw";
    private static final String URL_PRE = "https://www.google.com/speech-api/v2/recognize";
    private static final String URL_SEARCH = "https://www.google.com/search?q=";

    private List<Pair<String, Double>> getTextForLastUtteranceInternal() {
        try {

            HttpResponse<JsonNode> jsonResponse = Unirest.post(URL_PRE)
                    .header("accept", "application/json")
                    .queryString("output", "json")
                    .queryString("lang", "en-us")
                    .queryString("key", API_KEY)
                    .header("Content-Type", "audio/l16; rate=16000;")
                    .body(Files.readAllBytes(getLastRecordedUtterance().toPath()))
                    .asJson();

            // This is a ass-hack, due to the fact, that GSAPI responds
            // chunking-ly with two JSON objects instead of single one
            return parseGSAPIResponse(slurp(jsonResponse.getRawBody()));

        } catch (IOException | UnirestException | JSONException e) {
            logger.log(Level.SEVERE, "Panic! Failed process response of GSAPI!",e);
        }

        return Collections.emptyList();
    }

    Pair<String, Double> getBestTextForLastUtterance() {
        Pair<String, Double> best = null;
        for (Pair<String, Double> p : getTextForLastUtteranceInternal()) {
            if (best == null || p.second != null && best.second == null || p.second != null && best.second != null && p.second > best.second) {
                best = p;
            }
        }

        return best;
    }

    private List<Pair<String, Double>> parseGSAPIResponse(String r) throws JSONException {
        JSONObject o = new JSONObject(r.substring(13));
        JSONArray results = o.getJSONArray("result");

        List<Pair<String, Double>> res = new ArrayList<>();

        for (int i = 0; i < results.length(); ++i) {
            JSONArray as = ((JSONObject) results.get(i)).getJSONArray("alternative");
            for (int j = 0; j < as.length(); ++j) {
                JSONObject a = (JSONObject) as.get(j);
                res.add(Pair.create(a.getString("transcript"), a.has("confidence") ? a.getDouble("confidence") : 0));
            }
        }

        return res;
    }

    private String slurp(InputStream is) throws IOException {
        StringBuilder b = new StringBuilder();
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        String in;
        while (null != (in = br.readLine())) {
            b.append(in);
        }

        return b.toString();
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
        System.out.println(new GoogleService().getTextForLastUtteranceInternal());
    }
}
