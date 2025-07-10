package com.pharmazen.controllers;

import com.google.firebase.auth.FirebaseAuthException;
import com.pharmazen.services.AuthenticationService;
import com.pharmazen.services.EmailService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class RegisterPage {

    @FXML
    private TextField emailField;

    @FXML
    private Hyperlink loginLink;

    @FXML
    private Text messageText;

    @FXML
    private TextField nameField;

    @FXML
    private RadioButton noButton;

    @FXML
    private RadioButton yesButton;

    @FXML
    private ToggleGroup verificationToggleGroup;

    @FXML
    private PasswordField passField;

    @FXML
    private PasswordField reEnterPassField;

    @FXML
    private AnchorPane registerPane;

    @FXML
    private Button submitButton;

    @FXML
    private void handleLogin(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/com/pharmazen/loginPage.fxml")));
            Stage stage = (Stage) registerPane.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);

            scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/com/pharmazen/css/loginPage.css")).toExternalForm());
            Image icon = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/com/pharmazen/images/icon.png")));
            stage.getIcons().add(icon);

            stage.setTitle("PharmaZen Login");
            stage.centerOnScreen();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            messageText.setText("Cannot go to Login Dialog");
            System.out.println("Error message: " + e);
        } catch (NullPointerException e) {
            System.out.println(getClass().getResource("/com/pharmazen/loginPage.fxml"));
            System.out.println(getClass().getResource("/com/pharmazen/css/loginPage.css"));
            System.out.println(getClass().getResourceAsStream("/com/pharmazen/images/icon.png"));
        }
    }

    @FXML
    private void handleSubmit(ActionEvent event) {
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String password = passField.getText();
        String rePassword = reEnterPassField.getText();

        if (name.isEmpty() || email.isEmpty() || password.isEmpty() || rePassword.isEmpty()) {
            messageText.setText("Please fill the empty fields");
            return;
        }

        if (!password.equals(rePassword)) {
            messageText.setText("Passwords do not match");
            return;
        }

        RadioButton selectedRadio = (RadioButton) verificationToggleGroup.getSelectedToggle();
        if (selectedRadio == null) {
            messageText.setText("Please select an option");
            return;
        }

        try {
            messageText.setText("");
            boolean isDoctor = selectedRadio.getText().equalsIgnoreCase("Yes");

            AuthenticationService.register(name, email, password, isDoctor);
            System.out.println("Registration Successful");
            handleLogin(event);

        } catch (FirebaseAuthException e) {
            messageText.setText("Registration Failed: " + e.getMessage());
            System.out.println("Registration Failed: " + e.getMessage());
        }
    }


}
