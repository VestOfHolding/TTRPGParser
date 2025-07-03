package org.voh;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class TtrpgParserApp extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/org/voh/RPGParser.fxml")
        );
        Parent root = loader.load();

        // 2) Create the scene, size it to your needs
        Scene scene = new Scene(root, 800, 600);

        // 4) Configure the Stage
        primaryStage.setTitle("TTRPG Text Parser");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}