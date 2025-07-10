package com.pharmazen.controllers;

import com.google.firebase.auth.FirebaseAuthException;
import com.pharmazen.services.AuthenticationService;
import com.pharmazen.services.EmailService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class LoginPage {

    @FXML
    private TextField emailField;

    @FXML
    private Text errorMessage;

    @FXML
    private Button loginButton;

    @FXML
    private PasswordField passField;

    @FXML
    private AnchorPane rootPane;

    @FXML
    private Hyperlink gotoRegistration;

    @FXML
    private void handleLogin(ActionEvent event) {
        String email = emailField.getText().trim();
        String password = passField.getText().trim();

        if (email.isEmpty() || password.isEmpty()) {
            errorMessage.setText("Please fill the empty fields");
        } else {
            errorMessage.setText("");

            try{
                if(AuthenticationService.isUserExist(email)){
                    String uid = AuthenticationService.authenticate(email, password);

                    String otp = AuthenticationService.generateOtp();
                    AuthenticationService.storeOtp(uid, otp);
                    String name = AuthenticationService.getName(uid);
                    EmailService.sendOtp(email, name, otp);

                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/pharmazen/otpPage.fxml"));
                    Parent root = loader.load();

                    OtpPage otpController = loader.getController();
                    otpController.initData(uid, email);

                    Stage stage = (Stage) rootPane.getScene().getWindow();
                    Scene scene = new Scene(root);
                    scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/com/pharmazen/css/otpPage.css")).toExternalForm());

                    Image icon = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/com/pharmazen/images/icon.png")));
                    stage.getIcons().add(icon);

                    stage.setTitle("PharmaZen OTP Verification");
                    stage.setScene(scene);
                    stage.centerOnScreen();
                    stage.show();
                } else{
                    errorMessage.setText("Invalid Email or Password");
                }
            } catch (Exception e) {
                System.out.println("Error message: " + e);
                errorMessage.setText("Invalid Email or Password");
            }
        }
    }

    @FXML
    private void handleRegistration(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/com/pharmazen/registerPage.fxml")));
            Stage stage = (Stage) rootPane.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);

            scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/com/pharmazen/css/registerPage.css")).toExternalForm());
            Image icon = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/com/pharmazen/images/icon.png")));
            stage.getIcons().add(icon);

            stage.setTitle("PharmaZen Registration");
            stage.setScene(scene);
            stage.centerOnScreen();
            stage.show();
        } catch (IOException e) {
            errorMessage.setText("Cannot go to Registration Dialog");
            System.out.println("Error message: " + e);
        } catch (NullPointerException e){
            System.out.println(getClass().getResource("/com/pharmazen/registerPage.fxml"));
            System.out.println(getClass().getResource("/com/pharmazen/css/registerPage.css"));
            System.out.println(getClass().getResourceAsStream("/com/pharmazen/images/icon.png"));
        }
    }
}