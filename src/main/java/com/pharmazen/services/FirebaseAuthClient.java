package com.pharmazen.services;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class FirebaseAuthClient {

    private static final String API_KEY = "Copy the API KEY from Firebase here...";

    public static String signInAndGetUid(String email, String password) throws Exception {
        String endpoint = "https://identitytoolkit.googleapis.com/v1/accounts:signInWithPassword?key=" + API_KEY;
        URL url = new URL(endpoint);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);

        String payload = String.format(
                "{\"email\":\"%s\", \"password\":\"%s\", \"returnSecureToken\":true}",
                email, password
        );

        try (OutputStream os = conn.getOutputStream()) {
            os.write(payload.getBytes());
            os.flush();
        }

        int responseCode = conn.getResponseCode();
        Scanner scanner = new Scanner((responseCode == 200) ? conn.getInputStream() : conn.getErrorStream());
        String response = scanner.useDelimiter("\\A").next();
        scanner.close();

        if (responseCode == 200) {
            // Parse JSON to get uid
            JsonObject json = JsonParser.parseString(response).getAsJsonObject();
            String uid = json.get("localId").getAsString(); // This is Firebase UID
            return uid;
        } else {
            JsonObject json = JsonParser.parseString(response).getAsJsonObject();
            String message = json.getAsJsonObject("error").get("message").getAsString();
            throw new Exception("Login failed: " + message);
        }
    }
}

