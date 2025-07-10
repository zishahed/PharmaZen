package com.pharmazen.controllers;

import com.pharmazen.services.AuthenticationService;
import com.pharmazen.services.EmailService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

public class OtpPage {

    @FXML
    private TextField otpField;

    @FXML
    private AnchorPane otpPane;

    @FXML
    private Text messageText;

    private String uid;
    private String email;

    public void initData(String uid, String email) {
        this.uid = uid;
        this.email = email;
    }

    @FXML
    void resendOTP(ActionEvent event) {
        String otp = AuthenticationService.generateOtp();
        AuthenticationService.storeOtp(email, otp);
        String name = AuthenticationService.getName(uid);
        EmailService.sendOtp(email, name, otp);
        messageText.setText("check your email");
    }

    @FXML
    private void verifyOTP(ActionEvent actionEvent) throws ExecutionException, InterruptedException {
        messageText.setText(null);
        String enteredOtp = otpField.getText().trim();
        if (enteredOtp.isEmpty()) {
            messageText.setText("Please enter the OTP");
        } else {
            boolean isValid = AuthenticationService.verifyOtp(uid, enteredOtp);
            if (isValid) {
                messageText.setText("OTP Verified Successfully!");

                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/pharmazen/dashboard.fxml"));
                    Parent root = loader.load();

                    Stage stage = (Stage) otpPane.getScene().getWindow(); // get current stage
                    Scene scene = new Scene(root);
                    scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/com/pharmazen/css/dashboard.css")).toExternalForm());

                    Dashboard controller = loader.getController();
                    controller.setCurrentUserUid(uid);

                    stage.setScene(scene);
                    stage.centerOnScreen();
                    stage.setTitle("PharmaZen Dashboard");
                    stage.show();

                } catch (IOException e) {
                    messageText.setText("Failed to load dashboard.");
                    System.out.println("Error: " + e);
                } catch (NullPointerException e){
                    System.out.println(getClass().getResource("/com/pharmazen/dashboard.fxml"));
                    System.out.println(getClass().getResource("/com/pharmazen/css/dashboard.css"));
                    System.out.println(getClass().getResourceAsStream("/com/pharmazen/images/icon.png"));
                }

            } else {
                messageText.setText("Invalid OTP, please try again.");
            }
        }
    }
}

