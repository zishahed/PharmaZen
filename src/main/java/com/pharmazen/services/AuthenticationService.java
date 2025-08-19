package com.pharmazen.services;

import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.FieldValue;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import com.google.firebase.cloud.FirestoreClient;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ExecutionException;

public class AuthenticationService {

    public static boolean isUserExist(String email) throws FirebaseAuthException {
        UserRecord userRecord = FirebaseAuth.getInstance().getUserByEmail(email);
        return userRecord != null;
    }

    public static void register(String name, String email, String password, Boolean isDoctor) throws FirebaseAuthException {
        UserRecord.CreateRequest request = new UserRecord.CreateRequest().setDisplayName(name).setEmail(email).setPassword(password);
        UserRecord userRecord = FirebaseAuth.getInstance().createUser(request);

        Firestore db = FirestoreClient.getFirestore();

        Map<String, Object> userMap = new HashMap<>();
        userMap.put("uid", userRecord.getUid());
        userMap.put("name", name);
        userMap.put("email", email);
        userMap.put("doctor", isDoctor);
        userMap.put("createdAt", FieldValue.serverTimestamp());

        db.collection("users").document(userRecord.getUid()).set(userMap);
    }

    public static String authenticate(String email, String password) throws Exception {
        return FirebaseAuthClient.signInAndGetUid(email, password);
    }


    // OTP functions
    public static String generateOtp() {
        int otp = 100000 + new Random().nextInt(900000);
        return String.valueOf(otp);
    }

    public static void storeOtp(String uid, String otp) {
        Firestore db = FirestoreClient.getFirestore();
        Map<String, Object> data = new HashMap<>();
        data.put("otp", otp);
        data.put("createdAt", System.currentTimeMillis());
        db.collection("otp").document(uid).set(data);
    }

    public static boolean verifyOtp(String uid, String inputOtp) throws ExecutionException, InterruptedException {
        Firestore db = FirestoreClient.getFirestore();
        DocumentSnapshot snapshot = db.collection("otp").document(uid).get().get();

        if (!snapshot.exists()) return false;

        String storedOtp = snapshot.getString("otp");
        Long timestamp = snapshot.getLong("createdAt");
        long now = System.currentTimeMillis();

        return storedOtp != null && storedOtp.equals(inputOtp) && (now - timestamp) < 5 * 60 * 1000;
    }

    public static String getName(String uid) {
        try {
            Firestore db = FirestoreClient.getFirestore();
            DocumentSnapshot doc = db.collection("users").document(uid).get().get(); // blocking call

            if (doc.exists()) {
                return doc.getString("name");
            } else {
                System.out.println("No user found with UID: " + uid);
                return null;
            }
        } catch (Exception e) {
            System.out.println("Error message: " + e);
            return null;
        }
    }

    public static String getEmail(String uid) {
        try {
            Firestore db = FirestoreClient.getFirestore();
            DocumentSnapshot doc = db.collection("users").document(uid).get().get(); // blocking call

            if (doc.exists()) {
                return doc.getString("email");
            } else {
                System.out.println("No user found with UID: " + uid);
                return null;
            }
        } catch (Exception e) {
            System.out.println("Error message: " + e);
            return null;
        }
    }

    public static boolean getProf(String uid) {
        try {
            Firestore db = FirestoreClient.getFirestore();
            DocumentSnapshot doc = db.collection("users").document(uid).get().get();

            if (doc.exists()) {
                return Boolean.TRUE.equals(doc.getBoolean("doctor"));
            } else {
                return false;
            }
        } catch (Exception e) {
            System.out.println("Error: " + e);
            return false;
        }
    }
}

