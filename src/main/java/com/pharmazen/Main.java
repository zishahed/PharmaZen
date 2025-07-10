package com.pharmazen;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class Main extends Application {
    @Override
    public void start(Stage stage) {
        try{
            FireBaseInit.FirebaseInit();

            Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("loginPage.fxml")));
            Scene scene = new Scene(root);

            scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/com/pharmazen/css/loginPage.css")).toExternalForm());
            Image icon = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/com/pharmazen/images/icon.png")));
            stage.getIcons().add(icon);

            stage.setTitle("PharmaZen Login");
            stage.centerOnScreen();
            stage.setScene(scene);
            stage.show();
        } catch(IOException e){
            System.out.println("Error message: " + e);
        } catch(NullPointerException e){
            System.out.println(getClass().getResource("loginPage.fxml"));
            System.out.println(getClass().getResource("/com/pharmazen/css/loginPage.css"));
            System.out.println(getClass().getResourceAsStream("/com/pharmazen/images/icon.png"));
        }
    }

    public static void main(String[] args) {
        launch();
    }
}
