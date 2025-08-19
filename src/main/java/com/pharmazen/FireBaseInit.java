package com.pharmazen;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class FireBaseInit {
    public static void FirebaseInit() {
        String path = "Path to the .json file of the serviceAccountKey from Firebase";
        try {
            FileInputStream serviceAccount = new FileInputStream(path);
            FirebaseOptions options = FirebaseOptions.builder().setCredentials(GoogleCredentials.fromStream(serviceAccount)).build();

            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options);
                System.out.println("Firebase initialized successfully.");
            } else {
                System.out.println("Firebase already initialized.");
            }
        } catch (FileNotFoundException e) {
            System.err.println("File not found at " + path);
        } catch (IOException e) {
            System.out.println("Error: " + e);
        }
    }
}
