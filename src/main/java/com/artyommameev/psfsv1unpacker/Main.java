package com.artyommameev.psfsv1unpacker;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

/**
 * The main application class.
 *
 * @author Artyom Mameev
 */
public class Main extends Application {

    public static final String APP_NAME = "PS_FS_V1-unpacker";
    private static Stage stage;

    /**
     * The main entry point of the application.
     *
     * @param args the input arguments.
     */
    public static void main(String[] args) {
        launch(args);
    }

    /**
     * Returns the primary stage of the application.
     *
     * @return the primary stage of the application.
     */
    public static Stage getPrimaryStage() {
        return stage;
    }

    /**
     * JavaFX entry point of the application.
     *
     * @param primaryStage a primary stage of the application.
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        stage = primaryStage;

        Parent root = FXMLLoader.load(getClass().getResource(
                "/fxml/Main.fxml"));

        primaryStage.setTitle(APP_NAME);
        primaryStage.setScene(new Scene(root, 390, 465));
        primaryStage.getIcons().add(new Image(
                Main.class.getResourceAsStream(
                        "/icon/icon.png")));
        primaryStage.show();
    }
}
