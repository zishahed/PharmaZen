package com.pharmazen.controllers;

import com.google.cloud.firestore.Firestore;
import com.google.firebase.cloud.FirestoreClient;
import com.pharmazen.services.AuthenticationService;
import com.pharmazen.services.EmailService;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

public class Authorization {
    @FXML
    private TextField uidField;

    @FXML
    private TextField genericField;

    @FXML
    private DatePicker validUntilPicker;

    @FXML
    private Label statusLabel;

    private String currentDoctorUid;

    @FXML
    public void handleAuthorizeSubmit() {
        String targetUid = uidField.getText().trim();
        String genericName = genericField.getText().trim();
        LocalDate validUntilDate = validUntilPicker.getValue();

        if (targetUid.isEmpty() || genericName.isEmpty() || validUntilDate == null) {
            statusLabel.setText("Please fill in all fields.");
            statusLabel.setTextFill(javafx.scene.paint.Color.RED);
            return;
        }

        Date expiry = Date.from(validUntilDate.atStartOfDay(ZoneId.systemDefault()).toInstant());

        Map<String, Object> data = new HashMap<>();
        data.put("authorizedTo", targetUid);
        data.put("genericName", genericName);
        data.put("authorizedBy", currentDoctorUid);
        data.put("authorizedAt", new Date());
        data.put("validUntil", expiry);

        Firestore db = FirestoreClient.getFirestore();

        String docId = targetUid + "_" + genericName.replace(" ", "_");
        db.collection("authorizations").document(docId).set(data).addListener(() -> {
            try {
                statusLabel.setText("Authorization successful!");
                statusLabel.setTextFill(javafx.scene.paint.Color.GREEN);
            } catch (Exception e) {
                statusLabel.setText("Failed: " + e.getMessage());
                statusLabel.setTextFill(javafx.scene.paint.Color.RED);
            }
        }, Runnable::run);

        String toEmail = AuthenticationService.getEmail(targetUid);
        String patientName = AuthenticationService.getName(targetUid);
        String doctorName = AuthenticationService.getName(currentDoctorUid);

        EmailService.sendEmailToPatient(toEmail, patientName, doctorName, genericName, expiry);
    }

    public void setCurrentDoctorUid(String uid) {
        this.currentDoctorUid = uid;
    }
}