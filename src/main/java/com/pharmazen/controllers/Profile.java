package com.pharmazen.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class Profile {

    @FXML
    private Label nameLabel;
    @FXML
    private Label emailLabel;
    @FXML
    private Label professionLabel;
    @FXML
    private Label uidLabel;

    public void setUserData(String name, String email, boolean isDoctor, String uid) {
        nameLabel.setText(name);
        emailLabel.setText(email);
        professionLabel.setText(isDoctor ? "Doctor" : "Other");
        uidLabel.setText(uid);
    }
}
