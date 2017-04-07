package com.jetbrains.idear;

import com.intellij.openapi.util.Pair;
import com.jetbrains.idear.recognizer.CustomMicrophone;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GoogleHelper {

    private static Logger logger = Logger.getLogger(GoogleHelper.class.getSimpleName());

//    private static final String API_KEY = "AIzaSyB0DS1X9_qkZw2keZWw9p9EwUb9cV2bYsw";
    private static final String URL_PRE = "https://www.google.com/speech-api/v2/recognize";
    private static final String URL_SEARCH = "https://www.google.com/search?q=";

    private static final String API_KEY = "AIzaSyDhaglUIfPFvtYqKKpmhLQfkeiBBzgT2XE";

    private static List<Pair<String, Double>> getRecognizedTextForUtteranceInternal(File utterance) {
        try {

            HttpResponse<JsonNode> jsonResponse = Unirest.post(URL_PRE)
                    .header("accept", "application/json")
                    .queryString("output", "json")
                    .queryString("lang", "en-us")
                    .queryString("key", API_KEY)
                    .header("Content-Type", "audio/l16; rate=16000;")
                    .body(Files.readAllBytes(utterance.toPath()))
                    .asJson();

            // This is a ass-hack, due to the fact, that GSAPI responds
            // chunking-ly with two JSON objects instead of single one
            return parseGSAPIResponse(slurp(jsonResponse.getRawBody()));

        } catch (IOException | UnirestException | JSONException e) {
            logger.log(Level.SEVERE, "Panic! Failed process response of GSAPI!",e);
        }

        return Collections.emptyList();
    }

    private static Pair<String, Double> getBestTextForUtteranceInternal(File file) {
        Pair<String, Double> best = null;
        for (Pair<String, Double> p : getRecognizedTextForUtteranceInternal(file)) {
            if (best == null || p.second != null && best.second == null || p.second != null && best.second != null && p.second > best.second) {
                best = p;
            }
        }

        if (best != null)
            logger.info("BEST/UTT: " + best);

        return best;
    }


    public static Pair<String, Double> getBestTextForUtterance(File file) {
        return getBestTextForUtteranceInternal(file);
    }

    private static List<Pair<String, Double>> parseGSAPIResponse(String r) throws JSONException {
        List<Pair<String, Double>> res = new ArrayList<>();
        logger.log(Level.INFO, r);
        if (r.length() < 30) {
            logger.log(Level.WARNING, "No result!");
            return res;
        }

        JSONObject o = new JSONObject(r.substring(13));
        JSONArray results = o.getJSONArray("result");

        for (int i = 0; i < results.length(); ++i) {
            JSONArray as = ((JSONObject) results.get(i)).getJSONArray("alternative");
            for (int j = 0; j < as.length(); ++j) {
                JSONObject a = (JSONObject) as.get(j);
                res.add(Pair.create(a.getString("transcript"), a.has("confidence") ? a.getDouble("confidence") : 0));
            }
        }

        return res;
    }

    private static String slurp(InputStream is) throws IOException {
        StringBuilder b = new StringBuilder();
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        String in;
        while (null != (in = br.readLine())) {
            b.append(in);
        }

        return b.toString();
    }

    public static void searchGoogle(String s) {
        try {
            Desktop.getDesktop().browse(java.net.URI.create(URL_SEARCH + URLEncoder.encode(s, "UTF-8")));
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        try {
            CustomMicrophone.Companion.recordFromMic(4500);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Pair<String, Double> searchQueryTuple = GoogleHelper.getBestTextForUtterance(new File("/tmp/X.wav"));

        System.out.println(searchQueryTuple.first);
    }
}
