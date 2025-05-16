package com.duta.library;

import com.duta.library.repository.Database;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        Database.init();
        Parent root = FXMLLoader.load(
            getClass().getResource("/com/duta/library/Listing.fxml")
        );
        stage.setScene(new Scene(root, 1280, 720));
        stage.setTitle("Duta Library");
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
